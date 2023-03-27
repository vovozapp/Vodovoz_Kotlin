package com.vodovoz.app.feature.favorite

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.FragmentMainFavoriteBinding
import com.vodovoz.app.databinding.FragmentMainFavoriteFlowBinding
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.adapter.CategoryTabsAdapter
import com.vodovoz.app.ui.adapter.PagingProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.decoration.CategoryTabsMarginDecoration
import com.vodovoz.app.ui.decoration.GridMarginDecoration
import com.vodovoz.app.ui.decoration.ListMarginDecoration
import com.vodovoz.app.ui.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.profile.ProfileFragmentDirections
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_main_favorite_flow

    private val binding: FragmentMainFavoriteFlowBinding by viewBinding {
        FragmentMainFavoriteFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: FavoriteFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private val categoryTabsController = CategoryTabsFlowController(categoryTabsClickListener())
    private val bestForYouController by lazy { BestForYouController(cartManager, likeManager, getProductsShowClickListener(), getProductsClickListener()) }
    private val favoritesController by lazy {
        FavoritesListController(viewModel, cartManager, likeManager, getProductsClickListener(), requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryTabsController.bind(binding.categoriesRecycler, space)
        bestForYouController.bind(binding.bestForYouRv)
        favoritesController.bind(binding.productRecycler, binding.refreshEmptyFavoriteContainer)

        observeUiState()
        observeResultLiveData()
        initSearch()
        observeChangeLayoutManager()
    }

    private fun observeChangeLayoutManager() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeChangeLayoutManager()
                .collect {
                    favoritesController.changeLayoutManager(it, binding.productRecycler, binding.imgViewMode)
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.observeUiState()
                .collect { state ->

                    bindHeader(state.data)

                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    val data = state.data
                    if (state.bottomItem != null && state.data.layoutManager == FavoriteFlowViewModel.LINEAR && state.page != 2) {
                        favoritesController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        favoritesController.submitList(data.itemsList)
                    }

                    if (state.error !is ErrorState.Empty) {
                        showError(state.error)
                    }

                }
        }
    }

    private fun bindHeader(state: FavoriteFlowViewModel.FavoriteState) {

        binding.imgViewMode.setOnClickListener { viewModel.changeLayoutManager() }

        if (state.favoriteCategory != null) {
            showContainer(true)
        }

        if (state.bestForYouCategoryDetailUI != null) {
            val homeProducts = HomeProducts(
                1,
                listOf(state.bestForYouCategoryDetailUI),
                ProductsSliderConfig(
                    containShowAllButton = false,
                    largeTitle = true
                ),
                HomeProducts.DISCOUNT
            )
            bestForYouController.submitList(listOf(homeProducts))
            showContainer(false)
        }

        val categoryUiList = state.favoriteCategory?.categoryUIList ?: emptyList()

        if (state.isAvailable) {
            bindTabsVisibility(categoryUiList.isNotEmpty())
        } else {
            bindTabsVisibility(false)
        }

        categoryTabsController.submitList(categoryUiList)

        binding.tvCategoryName.text = state.favoriteCategory?.name
        binding.tvProductAmount.text = state.favoriteCategory?.productAmount.toString()
        binding.availableTitle.text = state.availableTitle
        binding.notAvailableTitle.text = state.notAvailableTitle
        binding.availableContainer.isVisible = state.availableTitle != null || state.notAvailableTitle != null

        binding.tvSort.setOnClickListener { showBottomSortSettings(state.sortType) }
        binding.imgCategories.setOnClickListener {
            val category = state.favoriteCategory ?: return@setOnClickListener
            val id = state.selectedCategoryId ?: return@setOnClickListener
            showMiniCatalog(category, id)
        }

        binding.availableButton.setOnClickListener {
            binding.availableButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.availableButton.elevation = resources.getDimension(R.dimen.elevation_1)
            binding.notAvailableButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            binding.notAvailableButton.elevation = 0f
            viewModel.updateByIsAvailable(true)
        }

        binding.notAvailableButton.setOnClickListener {
            binding.notAvailableButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.notAvailableButton.elevation = resources.getDimension(R.dimen.elevation_1)
            binding.availableButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            binding.availableButton.elevation = 0f
            viewModel.updateByIsAvailable(false)
        }

    }

    private fun bindTabsVisibility(vis: Boolean) {
        when(vis) {
            true -> {
                binding.categoriesRecycler.visibility = View.VISIBLE
                binding.imgCategories.visibility = View.VISIBLE
            }
            else -> {
                binding.imgCategories.visibility = View.GONE
                binding.categoriesRecycler.visibility = View.GONE
            }
        }
    }

    private fun showContainer(bool: Boolean) {
        binding.emptyFavoriteContainer.isVisible = !bool
        binding.favoriteContainer.isVisible = bool
    }

    private fun categoryTabsClickListener() : CategoryTabsFlowClickListener {
        return object : CategoryTabsFlowClickListener {
            override fun onTabClick(id: Long) {
                viewModel.onTabClick(id)
            }
        }
    }

    private fun showMiniCatalog(categoryUI: CategoryUI, id: Long) = findNavController().navigate(
        FavoriteFragmentDirections.actionToMiniCatalogBottomFragment(
            categoryUI.categoryUIList.toTypedArray(),
            id
        )
    )

    private fun showBottomSortSettings(sortType: SortType) = findNavController().navigate(
        FavoriteFragmentDirections.actionToSortProductsSettingsBottomFragment(sortType.name)
    )

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(PaginatedProductsCatalogWithoutFiltersFragment.CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.onTabClick(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(PaginatedProductsCatalogWithoutFiltersFragment.SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateBySortType(SortType.valueOf(sortType))
            }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(FavoriteFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                when (viewModel.isLoginAlready()) {
                    true -> findNavController().navigate(
                        FavoriteFragmentDirections.actionToPreOrderBS(
                            id,
                            name,
                            detailPicture
                        )
                    )
                    false -> findNavController().navigate(FavoriteFragmentDirections.actionToProfileFragment())
                }
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

        }
    }

    private fun getProductsShowClickListener() : ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {}
            override fun showAllTopProducts(id: Long) {}
            override fun showAllNoveltiesProducts(id: Long) {}
            override fun showAllBottomProducts(id: Long) {}
        }
    }

    private fun initSearch() {
        binding.searchContainer.clSearchContainer.setOnClickListener {
            findNavController().navigate(FavoriteFragmentDirections.actionToSearchFragment())
        }
        binding.searchContainer.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                findNavController().navigate(FavoriteFragmentDirections.actionToSearchFragment())
            }
        }
    }

}

/*
@AndroidEntryPoint
class FavoriteFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentMainFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModels()

    private var viewMode: PagingProductsAdapter.ViewMode = PagingProductsAdapter.ViewMode.LIST

    private val compositeDisposable = CompositeDisposable()

    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val updateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()

    private var productAdapter: PagingProductsAdapter = PagingProductsAdapter(
        onProductClick = {
            findNavController().navigate(FavoriteFragmentDirections.actionToProductDetailFragment(it))
        },
        onChangeFavoriteStatus = { productId, status ->
            viewModel.changeFavoriteStatus(productId, status)
        },
        onChangeCartQuantity = { productId, quantity ->
            viewModel.changeCart(productId, quantity)
        },
        onNotAvailableMore = {},
        onNotifyWhenBeAvailable = { id, name, picture ->
            findNavController().navigate(FavoriteFragmentDirections.actionToPreOrderBS(
                id, name, picture
            ))
        },
        productDiffItemCallback = ProductDiffItemCallback(),
        viewMode = viewMode
    )

    private val bestForYouProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false,
            largeTitle = true
        )) }

    private val onCategoryClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val categoryTabsAdapter = CategoryTabsAdapter(onCategoryClickSubject)

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private lateinit var categoriesLinearLayoutManager: LinearLayoutManager

    //Linear
    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(requireContext())
    }
    private val linearMarginDecoration: ListMarginDecoration by lazy {
        ListMarginDecoration((space*0.8).toInt())
    }
    private val linearDividerItemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(requireContext(), linearLayoutManager.orientation)
    }

    //Grid
    private val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup =  object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == productAdapter.itemCount && productAdapter.itemCount > 0) 2
                    else 1
                }
            }
        }
    }
    private val gridMarginDecoration: GridMarginDecoration by lazy {
        GridMarginDecoration(space)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateFavoriteProductsHeader()
        subscribeSubjects()
    }

    private fun subscribeSubjects() {
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(FavoriteFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)
        onFavoriteClickSubject.subscribeBy { pair ->
            viewModel.changeFavoriteStatus(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair.first, pair.second)
        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainFavoriteBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun initView() {
        onStateSuccess()
        setHasOptionsMenu(true)
        initProductRecycler()
        observeResultLiveData()
        initHeader()
        initSearch()
        initCategoryRecycler()
        initBestForYouProductsSlider()
        observeViewModel()
    }

    override fun update() { viewModel.updateFavoriteProductsHeader() }

    private fun initHeader() {
        binding.imgViewMode.setOnClickListener { changeViewMode() }
        binding.tvSort.setOnClickListener { showBottomSortSettings() }
        binding.imgCategories.setOnClickListener { showMiniCatalog() }
        binding.availableButton.setOnClickListener {
            binding.availableButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.availableButton.elevation = resources.getDimension(R.dimen.elevation_1)
            binding.notAvailableButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            binding.notAvailableButton.elevation = 0f
            viewModel.updateIsAvailable(true)
        }
        binding.notAvailableButton.setOnClickListener {
            binding.notAvailableButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.notAvailableButton.elevation = resources.getDimension(R.dimen.elevation_1)
            binding.availableButton.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            binding.availableButton.elevation = 0f
            viewModel.updateIsAvailable(false)
        }
    }

    private fun initSearch() {
        binding.searchContainer.clSearchContainer.setOnClickListener {
            findNavController().navigate(FavoriteFragmentDirections.actionToSearchFragment())
        }
        binding.searchContainer.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                findNavController().navigate(FavoriteFragmentDirections.actionToSearchFragment())
            }
        }
    }

    private fun initBestForYouProductsSlider() {
        childFragmentManager.beginTransaction().replace(
            R.id.bestForYouProductSliderFragment,
            bestForYouProductsSliderFragment
        ).commit()

        bestForYouProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId) },
            iOnChangeProductQuantity = {},
            iOnShowAllProductsClick = {},
            iOnFavoriteClick = { pair -> viewModel.changeFavoriteStatus(pair.first, pair.second) },
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                when(viewModel.isAlreadyLogin()) {
                    true -> findNavController().navigate(FavoriteFragmentDirections.actionToPreOrderBS(id, name, picture))
                    false -> findNavController().navigate(FavoriteFragmentDirections.actionToProfileFragment())
                }
            }
        )
    }

    private fun initCategoryRecycler() {
        categoriesLinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecycler.layoutManager = categoriesLinearLayoutManager
        binding.categoriesRecycler.adapter = categoryTabsAdapter
        binding.categoriesRecycler.addItemDecoration(CategoryTabsMarginDecoration(space/2))
        onCategoryClickSubject.subscribe { categoryId ->
            viewModel.updateCategoryId(categoryId)
        }
    }

    private fun initProductRecycler() {
        changeViewMode()
        updateSubject.subscribeBy { productAdapter.retry() }
        binding.productRecycler.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    binding.appBar.elevation =
                        if (binding.productRecycler.canScrollVertically(-1)) 16f
                        else 0f
                }
            }
        )
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(PaginatedProductsCatalogWithoutFiltersFragment.CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.updateCategoryId(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(PaginatedProductsCatalogWithoutFiltersFragment.SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateSortType(SortType.valueOf(sortType))
            }
    }

    private fun observeViewModel() {
        viewModel.sortTypeLD.observe(viewLifecycleOwner) { sortType ->
            binding.tvSort.text = sortType.sortName
        }

        viewModel.availableTitleLD.observe(viewLifecycleOwner) { availableTitle ->
            binding.availableTitle.text = availableTitle
        }

        viewModel.notAvailableTitleLD.observe(viewLifecycleOwner) { notAvailableTitle ->
            binding.notAvailableTitle.text = notAvailableTitle
        }

        viewModel.isShowAvailableSettingsLD.observe(viewLifecycleOwner) { isShow ->
            when(isShow) {
                true -> binding.availableContainer.visibility = View.VISIBLE
                false -> binding.availableContainer.visibility = View.GONE
            }
        }

        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> {}
                is ViewState.Hide -> onStateHide()
            }
        }

        viewModel.favoriteCategoryUILD.observe(viewLifecycleOwner) { categoryUI ->
            binding.emptyFavoriteContainer.visibility = View.INVISIBLE
            binding.favoriteContainer.visibility = View.VISIBLE
            updateHeader(categoryUI)
            updatePager()
        }

        viewModel.bestForYouCategoryDetailUILD.observe(viewLifecycleOwner) { categoryDetailUI ->
            binding.emptyFavoriteContainer.visibility = View.VISIBLE
            binding.favoriteContainer.visibility = View.INVISIBLE
            bestForYouProductsSliderFragment.updateData(listOf(categoryDetailUI))
            onStateSuccess()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHeader(categoryUI: CategoryUI) {
        binding.tvCategoryName.text = categoryUI.name
        binding.tvProductAmount.text = categoryUI.productAmount.toString()

        when(categoryUI.categoryUIList.isNotEmpty()) {
            true -> {
                binding.categoriesRecycler.visibility = View.VISIBLE
                binding.imgCategories.visibility = View.VISIBLE
            }
            else -> {
                binding.imgCategories.visibility = View.GONE
                binding.categoriesRecycler.visibility = View.GONE
            }
        }

        categoryTabsAdapter.categoryUIList = categoryUI.categoryUIList
        categoryTabsAdapter.selectedId = viewModel.categoryId
        categoryTabsAdapter.notifyDataSetChanged()

        val index = viewModel.categoryUIList.indexOfFirst { it.id == viewModel.categoryId }
        categoriesLinearLayoutManager.scrollToPosition(index)
    }

    private fun updatePager() {
        productAdapter = PagingProductsAdapter(
            onProductClick = {
                findNavController().navigate(ProfileFragmentDirections.actionToProductDetailFragment(it))
            },
            onChangeFavoriteStatus = { productId, status ->
                viewModel.changeFavoriteStatus(productId, status)
            },
            onChangeCartQuantity = { productId, quantity ->
                viewModel.changeCart(productId, quantity)
            },
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture ->
                findNavController().navigate(FavoriteFragmentDirections.actionToPreOrderBS(
                    id, name, picture
                ))
            },            productDiffItemCallback = ProductDiffItemCallback(),
            viewMode = viewMode
        )

        productAdapter.addLoadStateListener { state ->
            when (state.refresh) {
                is LoadState.Loading -> {}
                is LoadState.Error -> onStateError((state.refresh as LoadState.Error).error.message)
                is LoadState.NotLoading -> onStateSuccess()
            }
        }

        binding.productRecycler.adapter = productAdapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(updateSubject),
            footer = LoadStateAdapter(updateSubject)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateFavoriteProducts().collectLatest { productList ->
                Toast.makeText(requireContext(), "productList", Toast.LENGTH_SHORT).show()
                productAdapter.submitData(productList)
            }
        }
    }

    private fun updateProductRecyclerDecorationByViewMode() {
        with(binding.productRecycler) {
            when (viewMode) {
                PagingProductsAdapter.ViewMode.GRID -> {
                    removeItemDecoration(linearMarginDecoration)
                    removeItemDecoration(linearDividerItemDecoration)
                    addItemDecoration(gridMarginDecoration)
                }
                PagingProductsAdapter.ViewMode.LIST -> {
                    removeItemDecoration(gridMarginDecoration)
                    addItemDecoration(linearMarginDecoration)
                    addItemDecoration(linearDividerItemDecoration)
                }
            }
        }
    }

    private fun changeViewMode() {
        var firstVisiblePosition = -1
        var lastVisiblePosition = -1

        when (viewMode) {
            PagingProductsAdapter.ViewMode.GRID -> {
                viewMode = PagingProductsAdapter.ViewMode.LIST
                binding.imgViewMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_list))
                firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition()
                lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition()
                binding.productRecycler.layoutManager = linearLayoutManager
            }
            PagingProductsAdapter.ViewMode.LIST -> {
                viewMode = PagingProductsAdapter.ViewMode.GRID
                binding.imgViewMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_table))
                firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()
                lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()
                binding.productRecycler.layoutManager = gridLayoutManager
            }
        }
        productAdapter.viewMode = viewMode
        updateProductRecyclerDecorationByViewMode()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            productAdapter.notifyItemChanged(position)
        }

        linearLayoutManager.scrollToPositionWithOffset(firstVisiblePosition, 0)
        gridLayoutManager.scrollToPositionWithOffset(firstVisiblePosition, 0)
    }

    private fun showBottomSortSettings() = findNavController().navigate(
        FavoriteFragmentDirections.actionToSortProductsSettingsBottomFragment(viewModel.sortType.name)
    )

    private fun showMiniCatalog() = findNavController().navigate(
        FavoriteFragmentDirections.actionToMiniCatalogBottomFragment(
            viewModel.categoryUIList.toTypedArray(),
            viewModel.categoryId!!
        )
    )

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()

    }
}*/
