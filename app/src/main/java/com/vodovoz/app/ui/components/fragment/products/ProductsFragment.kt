package com.vodovoz.app.ui.components.fragment.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
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
import com.vodovoz.app.databinding.FragmentProductsBinding
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.components.base.FetchStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.components.adapter.primaryProductsFiltersAdapter.ProductsFiltersAdapter
import com.vodovoz.app.ui.components.adapter.primaryProductsFiltersAdapter.ProductsFiltersMarginDecoration
import com.vodovoz.app.ui.components.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.PagingProductsAdapter
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.ViewMode
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.grid.GridMarginDecoration
import com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list.ListMarginDecoration
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.custom.FilterBundleUI
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ProductsFragment : FetchStateBaseFragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SORT_TYPE = "SORT_TYPE"
        const val FILTER_BUNDLE = "FILTER_BUNDLE"
    }

    private lateinit var binding: FragmentProductsBinding
    private lateinit var viewModel: ProductsViewModel

    private var viewMode: ViewMode = ViewMode.LIST
    private val updateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private var productAdapter: PagingProductsAdapter = PagingProductsAdapter(
        onProductClickSubject = onProductClickSubject,
        productDiffItemCallback = ProductDiffItemCallback(),
        viewMode = viewMode
    )

    private val onBrandClickSubject: PublishSubject<FilterValueUI> = PublishSubject.create()
    private val productsFiltersAdapter = ProductsFiltersAdapter(onBrandClickSubject)

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }

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
//    private val gridVerticalDividerItemDecoration: DividerItemDecoration by lazy {
//        DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
//    }
//    private val gridHorizontalDividerItemDecoration: DividerItemDecoration by lazy {
//        DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
        initOnProductClick()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[ProductsViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(ProductsFragmentArgs.fromBundle(requireArguments()).categoryId)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentProductsBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

    override fun onStop() {
        super.onStop()
        binding.productRecycler.layoutManager = null
    }

    override fun initView() {
        setHasOptionsMenu(true)
        initProductRecycler()
        initBackButton()
        observeResultLiveData()
        initHeader()
        initBrandRecycler()
        observeViewModel()
    }

    override fun update() { viewModel.updateCategoryHeader() }

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
        binding.viewMode.setOnClickListener { changeViewMode() }
        binding.sort.setOnClickListener { showBottomSortSettings() }
        binding.filter.setOnClickListener { showAllFiltersFragment() }
        binding.categoryContainer.setOnClickListener { showMiniCatalog() }
        binding.back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initOnProductClick() {
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(ProductsFragmentDirections.actionProductsFragmentToProductDetailFragment(productId))
        }
    }

    private fun initBrandRecycler() {
        binding.brandRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.brandRecycler.adapter = productsFiltersAdapter
        binding.brandRecycler.addItemDecoration(ProductsFiltersMarginDecoration(space))
        onBrandClickSubject.subscribeBy { filterValue ->
            viewModel.addPrimaryFilterValue(filterValue)
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
            ?.getLiveData<Long>(CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.updateArgs(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateSortType(SortType.valueOf(sortType))
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FilterBundleUI>(FILTER_BUNDLE)?.observe(viewLifecycleOwner) { filterBundle ->
                viewModel.updateFilterBundle(filterBundle)
            }
    }

    private fun observeViewModel() {
        viewModel.sortTypeLD.observe(viewLifecycleOwner) { sortType ->
            binding.sort.text = sortType.sortName
        }

        viewModel.filtersAmountLD.observe(viewLifecycleOwner) { amount ->
            when(amount) {
                0 -> binding.filterAmount.visibility = View.INVISIBLE
                else -> {
                    binding.filterAmount.text = amount.toString()
                    binding.filterAmount.visibility = View.VISIBLE
                }
            }
        }

        viewModel.fetchStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is FetchState.Loading -> onStateLoading()
                is FetchState.Error -> onStateError(state.errorMessage)
                is FetchState.Success -> {
                    state.data?.let {
                        updateHeader(state.data)
                        updatePager()
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHeader(categoryUI: CategoryUI) {
        binding.categoryName.text = categoryUI.name
        binding.productAmount.text = categoryUI.productAmount.toString()
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

        productsFiltersAdapter.brandFilterValueList = categoryUI.primaryFilterValueList
        productsFiltersAdapter.notifyDataSetChanged()
    }

    private fun updatePager() {
        productAdapter = PagingProductsAdapter(
            onProductClickSubject = onProductClickSubject,
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
            header = LoadStateAdapter(updateSubject),
            footer = LoadStateAdapter(updateSubject)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateProductList().collectLatest { productList ->
                productAdapter.submitData(productList)
            }
        }
    }

    private fun updateProductRecyclerDecorationByViewMode() {
        with(binding.productRecycler) {
            when (viewMode) {
                ViewMode.GRID -> {
                    removeItemDecoration(linearMarginDecoration)
                    removeItemDecoration(linearDividerItemDecoration)
                    addItemDecoration(gridMarginDecoration)
                }
                ViewMode.LIST -> {
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
            ViewMode.GRID -> {
                viewMode = ViewMode.LIST
                binding.viewMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_list))
                firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition()
                lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition()
                binding.productRecycler.layoutManager = linearLayoutManager
            }
            ViewMode.LIST -> {
                viewMode = ViewMode.GRID
                binding.viewMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_table))
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
        ProductsFragmentDirections.actionProductsFragmentToSortSettingsBottomFragment2(viewModel.sortType.name)
    )

    private fun showAllFiltersFragment() = findNavController().navigate(
        ProductsFragmentDirections.actionProductsFragmentToFiltersFragment(
            viewModel.categoryId,
            viewModel.filterBundle
        )
    )

    private fun showMiniCatalog() = findNavController().navigate(
        ProductsFragmentDirections.actionProductsFragmentToSingleRootCatalogBottomFragment2(viewModel.categoryId)
    )

}