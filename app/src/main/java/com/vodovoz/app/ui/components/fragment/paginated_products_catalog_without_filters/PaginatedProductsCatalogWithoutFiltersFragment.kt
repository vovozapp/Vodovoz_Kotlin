package com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.vodovoz.app.databinding.FragmentProductsWithoutFiltersBinding
import com.vodovoz.app.ui.components.adapter.CategoryTabsAdapter
import com.vodovoz.app.ui.components.adapter.PagingProductsAdapter
import com.vodovoz.app.ui.components.adapter.PagingProductsAdapter.ViewMode
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.components.decoration.CategoryTabsMarginDecoration
import com.vodovoz.app.ui.components.decoration.GridMarginDecoration
import com.vodovoz.app.ui.components.decoration.ListMarginDecoration
import com.vodovoz.app.ui.components.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.components.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.components.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.components.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable


class PaginatedProductsCatalogWithoutFiltersFragment : ViewStateBaseFragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SORT_TYPE = "SORT_TYPE"
    }

    private lateinit var binding: FragmentProductsWithoutFiltersBinding
    private lateinit var viewModel: PaginatedProductsCatalogWithoutFiltersViewModel

    private var viewMode: ViewMode = ViewMode.LIST

    private val compositeDisposable = CompositeDisposable()

    private val onChangeProductQuantitySubject: PublishSubject<ProductUI> = PublishSubject.create()
    private val updateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()

    private var productAdapter: PagingProductsAdapter = PagingProductsAdapter(
        onProductClickSubject = onProductClickSubject,
        productDiffItemCallback = ProductDiffItemCallback(),
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        viewMode = viewMode
    )

    private val onCategoryClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val categoryTabsAdapter = CategoryTabsAdapter(onCategoryClickSubject)

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }

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
        getArgs()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[PaginatedProductsCatalogWithoutFiltersViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(PaginatedProductsCatalogWithoutFiltersFragmentArgs.fromBundle(requireArguments()).dataSource)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentProductsWithoutFiltersBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
    }.root

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
        binding.categories.setOnClickListener { showMiniCatalog() }
        binding.back.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initBrandRecycler() {
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
            ?.getLiveData<Long>(CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.updateCategoryId(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateSortType(SortType.valueOf(sortType))
            }
    }

    private fun observeViewModel() {
        viewModel.sortTypeLD.observe(viewLifecycleOwner) { sortType ->
            binding.sort.text = sortType.sortName
        }

        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> {}
                is ViewState.Hide -> onStateHide()
            }
        }

        viewModel.categoryUILD.observe(viewLifecycleOwner) { categoryUI ->
            updateHeader(categoryUI)
            updatePager()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHeader(categoryUI: CategoryUI) {
        binding.categoryName.text = categoryUI.name
        binding.productAmount.text = categoryUI.productAmount.toString()

        when(categoryUI.categoryUIList.isNotEmpty()) {
            true -> {
                binding.categoriesRecycler.visibility = View.VISIBLE
                binding.categories.visibility = View.VISIBLE
            }
            else -> {
                binding.categories.visibility = View.GONE
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
            onProductClickSubject = onProductClickSubject,
            productDiffItemCallback = ProductDiffItemCallback(),
            onChangeProductQuantitySubject = onChangeProductQuantitySubject,
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
            viewModel.updateProducts().collectLatest { productList ->
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
        PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToSortProductsSettingsBottomFragment(viewModel.sortType.name)
    )

    private fun showMiniCatalog() = findNavController().navigate(
        PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToMiniCatalogBottomFragment(
            viewModel.categoryUIList.toTypedArray(),
            viewModel.categoryId!!
        )
    )

    override fun onStart() {
        super.onStart()

        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(PaginatedProductsCatalogWithoutFiltersFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
        binding.productRecycler.layoutManager = null
    }

    sealed class DataSource : Serializable {
        class Brand(val brandId: Long) : DataSource()
        class Country(val countryId: Long) : DataSource()
        class Discount: DataSource()
        class Novelties : DataSource()
        class Slider(val categoryId: Long) : DataSource()
    }
}