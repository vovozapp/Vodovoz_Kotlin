package com.vodovoz.app.ui.fragment.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.FragmentSearchBinding
import com.vodovoz.app.databinding.ViewSimpleTextChipBinding
import com.vodovoz.app.ui.adapter.CategoryTabsAdapter
import com.vodovoz.app.ui.adapter.PagingProductsAdapter
import com.vodovoz.app.ui.adapter.PagingProductsAdapter.ViewMode
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.base.loadStateAdapter.LoadStateAdapter
import com.vodovoz.app.ui.decoration.CategoryTabsMarginDecoration
import com.vodovoz.app.ui.decoration.GridMarginDecoration
import com.vodovoz.app.ui.decoration.ListMarginDecoration
import com.vodovoz.app.ui.diffUtils.ProductDiffItemCallback
import com.vodovoz.app.ui.fragment.home.HomeFragmentDirections
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchFragment : ViewStateBaseFragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SORT_TYPE = "SORT_TYPE"
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel

    private val compositeDisposable = CompositeDisposable()

    private val onChangeProductQuantitySubject: PublishSubject<Pair<Long, Int>> = PublishSubject.create()
    private val onUpdateSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onFavoriteClickSubject: PublishSubject<Pair<Long, Boolean>> = PublishSubject.create()
    private val onCategoryClickSubject: PublishSubject<Long> = PublishSubject.create()

    private val categoryTabsAdapter = CategoryTabsAdapter(onCategoryClickSubject)
    private var productAdapter = PagingProductsAdapter(
        onProductClickSubject = onProductClickSubject,
        productDiffItemCallback = ProductDiffItemCallback(),
        onChangeProductQuantitySubject = onChangeProductQuantitySubject,
        onFavoriteClickSubject = onFavoriteClickSubject,
        viewMode = ViewMode.LIST
    )

    private lateinit var categoriesLinearLayoutManager: LinearLayoutManager
    private lateinit var productsGridLayoutManager: GridLayoutManager
    private lateinit var productLinearLayoutManager: LinearLayoutManager

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }
    private val linearMarginDecoration: ListMarginDecoration by lazy { ListMarginDecoration(space) }
    private val linearDividerItemDecoration: DividerItemDecoration by lazy { DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL) }
    private val gridMarginDecoration: GridMarginDecoration by lazy { GridMarginDecoration(space) }

    private val popularProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(containShowAllButton = false)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        subscribeSubjects()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[SearchViewModel::class.java]
        viewModel.updateDefaultSearchData()
    }

    private fun subscribeSubjects() {
        onCategoryClickSubject.subscribe { categoryId ->
            viewModel.updateCategoryId(categoryId)
        }.addTo(compositeDisposable)
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(SearchFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)
        onUpdateSubject.subscribeBy {
            productAdapter.retry()
        }.addTo(compositeDisposable)
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup,
    ) = FragmentSearchBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        setHasOptionsMenu(true)
        initProductRecycler()
        initProductsLayoutManagers()
        initPopularProductsSliderFragment()
        //initBackButtonListener()
        initShare()
        updateProductRecyclerDecorationByViewMode()
        initBackButton()
        observeResultLiveData()
        initHeader()
        initSearch()
        initCategoriesRecycler()
        observeViewModel()
    }

    override fun update() { viewModel.updateHeader() }

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

    private fun initCategoriesRecycler() {
        categoriesLinearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecycler.layoutManager = categoriesLinearLayoutManager
        binding.categoriesRecycler.adapter = categoryTabsAdapter
        binding.categoriesRecycler.addItemDecoration(CategoryTabsMarginDecoration(space))
    }

    private fun initProductRecycler() {
        binding.productRecycler.setScrollElevation(binding.appBar)
    }

    private fun initPopularProductsSliderFragment() {
        popularProductsSliderFragment.initCallbacks(
            iOnProductClick = { productId -> onProductClickSubject.onNext(productId) },
            iOnChangeProductQuantity = {},
            iOnFavoriteClick = { pair -> onFavoriteClickSubject.onNext(pair) },
            iOnShowAllProductsClick = {}
        )

        childFragmentManager.beginTransaction().replace(
            R.id.popularProductsSliderFragment,
            popularProductsSliderFragment
        ).commit()

    }

    private fun initBackButtonListener() {
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener { _, keyCode, _ ->
            Log.i(LogSettings.ID_LOG, keyCode.toString())
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (binding.searchDataContainer.visibility == View.VISIBLE) {
                    binding.searchDataContainer.visibility = View.INVISIBLE
                } else {
                    findNavController().popBackStack()
                }
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun initProductsLayoutManagers() {
        productLinearLayoutManager = LinearLayoutManager(requireContext())
        productsGridLayoutManager = GridLayoutManager(requireContext(), 2).apply {
            spanSizeLookup =  object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == productAdapter.itemCount && productAdapter.itemCount > 0) 2
                    else 1
                }
            }
        }
    }

    private fun initShare() {
        binding.share.setOnClickListener {
            Log.i(LogSettings.ID_LOG, viewModel.categoryHeader!!.shareUrl)
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, viewModel.categoryHeader!!.shareUrl)
                },
                "Shearing Option"
            ).let { startActivity(it) }
        }
    }

    private fun initSearch() {
        binding.clearSearchHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        binding.search.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                binding.searchDataContainer.visibility = View.VISIBLE
                binding.productsContainer.visibility = View.INVISIBLE
            }
        }

        binding.search.doAfterTextChanged { query ->
            when(query?.trim().toString().isNotEmpty()) {
                true -> binding.clear.visibility = View.VISIBLE
                false -> binding.clear.visibility = View.INVISIBLE
            }
        }

        binding.clear.setOnClickListener {
            binding.search.setText("")
            binding.search.requestFocus()
        }

        binding.search.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.i(LogSettings.ID_LOG, "SEARCH ID")
                val query = binding.search.text.trim().toString()
                Log.i(LogSettings.ID_LOG, "QUERY: $query")
                if (query.isNotEmpty()) {
                    Log.i(LogSettings.ID_LOG, "NOT EMPTY")
                    updateProductsByQuery(query)
                }
            }
            true
        }
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
                is ViewState.Success -> onStateSuccess()
                is ViewState.Hide -> onStateHide()
            }
        }

        viewModel.headerCategoryUILD.observe(viewLifecycleOwner) { categoryUI ->
            updateHeader(categoryUI)
            updatePager()
        }

        viewModel.historyQueryListLD.observe(viewLifecycleOwner) { queryList ->
            when(queryList.isEmpty()) {
                true -> binding.historyQueryContainer.visibility = View.GONE
                false -> {
                    binding.historyQueryContainer.visibility = View.VISIBLE
                    binding.historyQueryChipGroup.removeAllViews()
                    queryList.forEach { query ->
                        binding.historyQueryChipGroup.addView(buildQueryChip(query))
                    }

                }
            }
        }

        viewModel.popularQueryListLD.observe(viewLifecycleOwner) { queryList ->
            when(queryList.isEmpty()) {
                true -> binding.popularQueryContainer.visibility = View.GONE
                false -> {
                    binding.popularQueryContainer.visibility = View.VISIBLE
                    binding.popularQueryChipGroup.removeAllViews()
                    queryList.forEach { query ->
                        binding.popularQueryChipGroup.addView(buildQueryChip(query))
                    }

                }
            }

            SearchFragmentArgs.fromBundle(requireArguments()).query.let { query ->
                if (query.isNotEmpty()) {
                    updateProductsByQuery(query)
                }
            }
        }

        viewModel.popularCategoryDetailUILD.observe(viewLifecycleOwner) { categoryDetailUI ->
            popularProductsSliderFragment.updateData(listOf(categoryDetailUI))
        }
    }

    private fun buildQueryChip(query: String): Chip {
        val chip = ViewSimpleTextChipBinding.inflate(layoutInflater, null, false).root
        chip.text = query

        chip.setOnClickListener {
            updateProductsByQuery(query)
        }

        return chip
    }

    private fun updateProductsByQuery(query: String) {
        binding.searchDataContainer.visibility = View.INVISIBLE
        binding.productsContainer.visibility = View.VISIBLE
        binding.search.setText(query)
        viewModel.updateQuery(query)

        val view = requireActivity().currentFocus
        if (view != null) {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHeader(categoryUI: CategoryUI) {
        binding.categoryName.text = categoryUI.productAmount

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
            onFavoriteClickSubject = onFavoriteClickSubject,
            viewMode = productAdapter.viewMode
        )

        productAdapter.addLoadStateListener { state ->
            if (state.append.endOfPaginationReached) {
                if (productAdapter.itemCount == 0) {
                    binding.productsContainer.visibility = View.INVISIBLE
                }
            }

            when (state.refresh) {
                is LoadState.Loading -> {}
                is LoadState.Error -> onStateError((state.refresh as LoadState.Error).error.message)
                is LoadState.NotLoading -> onStateSuccess()
            }
        }

        binding.productRecycler.adapter = productAdapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(onUpdateSubject),
            footer = LoadStateAdapter(onUpdateSubject)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateProductsByQuery().collectLatest { productList ->
                binding.productsContainer.visibility = View.VISIBLE
                productAdapter.submitData(productList)
            }
        }
    }

    private fun updateProductRecyclerDecorationByViewMode() {
        with(binding.productRecycler) {
            layoutManager = when (productAdapter.viewMode) {
                ViewMode.GRID -> {
                    removeItemDecoration(linearMarginDecoration)
                    removeItemDecoration(linearDividerItemDecoration)
                    addItemDecoration(gridMarginDecoration)
                    productsGridLayoutManager
                }
                ViewMode.LIST -> {
                    removeItemDecoration(gridMarginDecoration)
                    addItemDecoration(linearMarginDecoration)
                    addItemDecoration(linearDividerItemDecoration)
                    productLinearLayoutManager
                }
            }
        }
    }

    private fun changeViewMode() {
        var firstVisiblePosition = -1
        var lastVisiblePosition = -1

        when (productAdapter.viewMode) {
            ViewMode.GRID -> {
                productAdapter.viewMode = ViewMode.LIST
                binding.viewMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_list))
                firstVisiblePosition = productsGridLayoutManager.findFirstVisibleItemPosition()
                lastVisiblePosition = productsGridLayoutManager.findLastVisibleItemPosition()
            }
            ViewMode.LIST -> {
                productAdapter.viewMode = ViewMode.GRID
                binding.viewMode.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_table))
                firstVisiblePosition = productLinearLayoutManager.findFirstVisibleItemPosition()
                lastVisiblePosition = productLinearLayoutManager.findLastVisibleItemPosition()
            }
        }

        updateProductRecyclerDecorationByViewMode()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            productAdapter.notifyItemChanged(position)
        }

        productLinearLayoutManager.scrollToPositionWithOffset(firstVisiblePosition, 0)
        productsGridLayoutManager.scrollToPositionWithOffset(firstVisiblePosition, 0)
    }

    private fun showBottomSortSettings() {
        findNavController().navigate(SearchFragmentDirections.actionToSortProductsSettingsBottomFragment(
            viewModel.sortType.name
        ))
    }

    private fun showMiniCatalog() {
        findNavController().navigate(SearchFragmentDirections.actionToMiniCatalogBottomFragment(
            viewModel.categoryUIList.toTypedArray(),
            viewModel.categoryId
        ))
    }

    override fun onResume() {
        super.onResume()
        binding.search.requestFocus()
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}