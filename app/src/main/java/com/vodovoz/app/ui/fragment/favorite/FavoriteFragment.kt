package com.vodovoz.app.ui.fragment.favorite

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.FragmentMainFavoriteBinding
import com.vodovoz.app.ui.adapter.CategoryTabsAdapter
import com.vodovoz.app.ui.adapter.PagingProductsAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.decoration.CategoryTabsMarginDecoration
import com.vodovoz.app.ui.decoration.GridMarginDecoration
import com.vodovoz.app.ui.decoration.ListMarginDecoration
import com.vodovoz.app.ui.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.fragment.cart.CartFragmentDirections
import com.vodovoz.app.ui.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.profile.ProfileFragmentDirections
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentMainFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel

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
        ListMarginDecoration(space)
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
        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[FavoriteViewModel::class.java]
        viewModel.updateFavoriteProductsHeader()
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
        binding.categoriesRecycler.addItemDecoration(CategoryTabsMarginDecoration(space))
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
}