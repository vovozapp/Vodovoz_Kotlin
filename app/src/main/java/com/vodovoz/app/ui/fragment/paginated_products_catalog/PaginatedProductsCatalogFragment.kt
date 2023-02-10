package com.vodovoz.app.ui.fragment.paginated_products_catalog

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.FragmentProductsBinding
import com.vodovoz.app.ui.adapter.PagingProductsAdapter
import com.vodovoz.app.ui.adapter.PagingProductsAdapter.ViewMode
import com.vodovoz.app.ui.adapter.PrimaryProductFiltersAdapter
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.decoration.ProductsFiltersMarginDecoration
import com.vodovoz.app.ui.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.fragment.profile.ProfileFragmentDirections
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaginatedProductsCatalogFragment : ViewStateBaseFragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SORT_TYPE = "SORT_TYPE"
        const val FILTER_BUNDLE = "FILTER_BUNDLE"
    }

    private lateinit var binding: FragmentProductsBinding
    private val viewModel: PaginatedProductsCatalogViewModel by viewModels()

    private val compositeDisposable = CompositeDisposable()

    private var viewMode: ViewMode = ViewMode.LIST
    private val onUpdateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()
    private val onBrandClickSubject: PublishSubject<FilterValueUI> = PublishSubject.create()

    private var productAdapter: PagingProductsAdapter = PagingProductsAdapter(
        onProductClick = {
            findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToProductDetailFragment(it))
        },
        onChangeFavoriteStatus = { productId, status ->
            viewModel.changeFavoriteStatus(productId, status)
        },
        onChangeCartQuantity = { productId, quantity ->
            viewModel.changeCart(productId, quantity)
        },
        onNotAvailableMore = {},
        onNotifyWhenBeAvailable = { id, name, picture ->
            findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToPreOrderBS(
                id, name, picture
            ))
        },
        productDiffItemCallback = ProductDiffItemCallback(),
        viewMode = viewMode
    )

    private val primaryProductFiltersAdapter = PrimaryProductFiltersAdapter(onBrandClickSubject)

    //Grid
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }
    private val linearMarginDecoration: RecyclerView.ItemDecoration by lazy {
        object  : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                with(outRect) {
                    top = space/2
                    bottom = space/2
                    right = space
                }
            }
        }
    }
    private val linearDividerItemDecoration: DividerItemDecoration by lazy {
        DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
    }
    private val gridMarginDecoration: RecyclerView.ItemDecoration by lazy {
        object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                with(outRect) {
                    if (parent.getChildAdapterPosition(view) % 2 == 0) {
                        left = space
                        right = space/2
                    } else {
                        left = space/2
                        right = space
                    }
                    top = space/2
                    bottom = space/2
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        getArgs()
        subscribeSubjects()
    }

    private fun subscribeSubjects() {
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)
        onFavoriteClickSubject.subscribeBy { pair ->
            viewModel.changeFavoriteStatus(pair.first, pair.second)
        }.addTo(compositeDisposable)
        onChangeProductQuantitySubject.subscribeBy { pair ->
            viewModel.changeCart(pair.first, pair.second)
        }.addTo(compositeDisposable)
    }

    private fun getArgs() {
        viewModel.updateArgs(PaginatedProductsCatalogFragmentArgs.fromBundle(requireArguments()).categoryId)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentProductsBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun update() { viewModel.updateCategoryHeader() }

    override fun initView() {
        initLayoutManagers()
        initProductsRecycler()
        initBackButton()
        observeResultLiveData()
        initHeader()
        initSearch()
        initPrimaryFiltersRecycler()
        observeViewModel()
    }

    private fun initLayoutManagers() {
        linearLayoutManager = LinearLayoutManager(requireContext())
        gridLayoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup =  object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == productAdapter.itemCount && productAdapter.itemCount > 0) 2
                    else 1
                }
            }
        }
    }

    private fun initBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun initHeader() {
        binding.imgViewMode.setOnClickListener { changeViewMode() }
        binding.tvSort.setOnClickListener { showBottomSortSettings() }
        binding.imgFilters.setOnClickListener { showAllFiltersFragment() }
        binding.categoryContainer.setOnClickListener { showSingleRootCatalogCatalog() }
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initSearch() {
        binding.incAppBar.incSearch.clSearchContainer.setOnClickListener {
            findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToSearchFragment())
        }
        binding.incAppBar.incSearch.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToSearchFragment())
            }
        }
    }

    private fun initPrimaryFiltersRecycler() {
        binding.brandRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.brandRecycler.adapter = primaryProductFiltersAdapter
        binding.brandRecycler.addItemDecoration(ProductsFiltersMarginDecoration(space/2))
        onBrandClickSubject.subscribeBy { filterValue ->
            viewModel.addPrimaryFilterValue(filterValue)
        }
    }

    private fun changeViewMode() {
        var firstVisiblePosition = -1
        var lastVisiblePosition = -1

        when (viewMode) {
            ViewMode.GRID -> {
                viewMode = ViewMode.LIST
                firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition()
                lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition()
            }
            ViewMode.LIST -> {
                viewMode = ViewMode.GRID
                firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()
                lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()
            }
        }

        productAdapter.viewMode = viewMode
        initProductsRecycler()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            productAdapter.notifyItemChanged(position)
        }

        linearLayoutManager.scrollToPositionWithOffset(firstVisiblePosition, 0)
        gridLayoutManager.scrollToPositionWithOffset(firstVisiblePosition, 0)
    }

    private fun initProductsRecycler() {
        binding.productRecycler.setScrollElevation(binding.appBar)
        when (viewMode) {
            ViewMode.LIST -> {
                viewMode = ViewMode.LIST
                binding.imgViewMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_list))
                binding.productRecycler.layoutManager = linearLayoutManager
                binding.productRecycler.removeItemDecoration(gridMarginDecoration)
                binding.productRecycler.addItemDecoration(linearMarginDecoration)
                binding.productRecycler.addItemDecoration(linearDividerItemDecoration)
            }
            ViewMode.GRID -> {
                viewMode = ViewMode.GRID
                binding.imgViewMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.png_table))
                binding.productRecycler.layoutManager = gridLayoutManager
                binding.productRecycler.removeItemDecoration(linearMarginDecoration)
                binding.productRecycler.removeItemDecoration(linearDividerItemDecoration)
                binding.productRecycler.addItemDecoration(gridMarginDecoration)
            }
        }
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.updateArgs(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateSortType(SortType.valueOf(sortType))
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FiltersBundleUI>(FILTER_BUNDLE)?.observe(viewLifecycleOwner) { filterBundle ->
                viewModel.updateFilterBundle(filterBundle)
            }
    }

    private fun observeViewModel() {
        viewModel.sortTypeLD.observe(viewLifecycleOwner) { sortType ->
            binding.tvSort.text = sortType.sortName
        }

        viewModel.filtersAmountLD.observe(viewLifecycleOwner) { amount ->
            when(amount) {
                0 -> binding.tvFiltersAmount.visibility = View.INVISIBLE
                else -> {
                    binding.tvFiltersAmount.text = amount.toString()
                    binding.tvFiltersAmount.visibility = View.VISIBLE
                }
            }
        }

        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> {}
            }
        }

        viewModel.categoryUILD.observe(viewLifecycleOwner) { categoryUI ->
            updateHeader(categoryUI)
            updatePager()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHeader(categoryUI: CategoryUI) {
        binding.tvCategoryName.text = categoryUI.name
        binding.tvProductAmount.text = categoryUI.productAmount.toString()
        binding.additionalName.text = categoryUI.primaryFilterName

        when(categoryUI.primaryFilterValueList.isNotEmpty()) {
            true -> {
                binding.brandTabsContainer.visibility = View.VISIBLE
                binding.changeCategoryContainer.visibility = View.GONE
            }
            else -> {
                binding.brandTabsContainer.visibility = View.GONE
                binding.changeCategoryContainer.visibility = View.VISIBLE
            }
        }

        primaryProductFiltersAdapter.brandFilterValueList = categoryUI.primaryFilterValueList
        primaryProductFiltersAdapter.notifyDataSetChanged()
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
                findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToPreOrderBS(
                    id, name, picture
                ))
            },
            productDiffItemCallback = ProductDiffItemCallback(),
            viewMode = viewMode
        )

        productAdapter.addLoadStateListener { state ->
            when (state.refresh) {
                is LoadState.Loading -> {}
                is LoadState.Error -> onStateError("Ошибка сети")
                is LoadState.NotLoading -> onStateSuccess()
            }
        }

        binding.productRecycler.adapter = productAdapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(onUpdateSubject),
            footer = LoadStateAdapter(onUpdateSubject)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateProductList().collectLatest { productList ->
                productAdapter.submitData(productList)
            }
        }
    }

    private fun showBottomSortSettings() = findNavController().navigate(
        PaginatedProductsCatalogFragmentDirections.actionToSortProductsSettingsBottomFragment(viewModel.sortType.name)
    )

    private fun showAllFiltersFragment() = findNavController().navigate(
        PaginatedProductsCatalogFragmentDirections.actionToProductFiltersFragment(
            viewModel.filterBundle,
            viewModel.categoryId
        )
    )

    private fun showSingleRootCatalogCatalog() = findNavController().navigate(
        PaginatedProductsCatalogFragmentDirections.actionToSingleRootCatalogBottomFragment(viewModel.categoryId)
    )

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}