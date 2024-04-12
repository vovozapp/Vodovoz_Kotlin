package com.vodovoz.app.feature.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.databinding.FragmentSearchFlowBinding
import com.vodovoz.app.databinding.ViewSimpleTextChipBinding
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.SortTypeListUI
import com.vodovoz.app.ui.model.SortTypeUI
import com.vodovoz.app.util.extensions.fromHtml
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SORT_TYPE = "SORT_TYPE"
    }

    override fun layout(): Int = R.layout.fragment_search_flow

    private val binding: FragmentSearchFlowBinding by viewBinding {
        FragmentSearchFlowBinding.bind(
            contentView
        )
    }

    internal val viewModel: SearchFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var tabManager: TabManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private val categoryTabsController by lazy {
        CategoryTabsFlowController(categoryTabsClickListener(), space)
    }
    private val bestForYouController by lazy {
        BestForYouController(
            cartManager,
            likeManager,
            getProductsShowClickListener(),
            getProductsClickListener()
        )
    }
    private val productsController by lazy {
        SearchFlowController(
            viewModel,
            cartManager,
            likeManager,
            getProductsClickListener(),
            requireContext(),
            ratingProductManager
        )
    }

    private val args: SearchFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeUiState()
        observeEvents()
        observeChangeLayoutManager()
        observeNoMatchesToast()
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.query.isNotEmpty()) {
            binding.incAppBar.incSearch.etSearch.setText(args.query)
            viewModel.fetchMatchesQueries(args.query)
        }

        categoryTabsController.bind(binding.categoriesRecycler)
        bestForYouController.bind(binding.bestForYouRv)
        productsController.bind(binding.productRecycler)

        observeResultLiveData()
        initBackButton()
        initSearch()
        bindErrorRefresh {
            viewModel.refreshSorted()
        }

    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect {
                        when (it) {
                            is SearchFlowViewModel.SearchEvents.GoToPreOrder -> {
                                if (findNavController().currentBackStackEntry?.destination?.id == R.id.preOrderBS) {
                                    findNavController().popBackStack()
                                }
                                findNavController().navigate(
                                    SearchFragmentDirections.actionToPreOrderBS(
                                        it.id,
                                        it.name,
                                        it.detailPicture
                                    )
                                )
                            }

                            is SearchFlowViewModel.SearchEvents.GoToProfile -> {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            }

                            SearchFlowViewModel.SearchEvents.GoToContacts -> {
                                findNavController().navigate(SearchFragmentDirections.actionToContactsFragment())
                            }

                            SearchFlowViewModel.SearchEvents.GoToPromotions -> {
                                findNavController().navigate(
                                    SearchFragmentDirections.actionToAllPromotionsFragment(
                                        AllPromotionsFragment.DataSource.All
                                    )
                                )
                            }

                            is SearchFlowViewModel.SearchEvents.GoToService -> {
                                findNavController().navigate(
                                    SearchFragmentDirections.actionToServiceDetailNewFragment(it.id)
                                )
                            }

                            is SearchFlowViewModel.SearchEvents.GoToWebView -> {
                                findNavController().navigate(
                                    SearchFragmentDirections.actionToWebViewFragment(
                                        it.url,
                                        it.title
                                    )
                                )
                            }

                        }
                    }
            }
        }
    }

    private fun observeNoMatchesToast() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeNoMatchesToast()
                    .collect {
                        if (it) {
                            //  Toast.makeText(requireContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show()
                            binding.emptyResultContainer.isVisible = true
                        }
                    }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun initSearch() {
        binding.incAppBar.incSearch.imgMicro.visibility = View.GONE
        binding.incAppBar.incSearch.imgQr.visibility = View.GONE
        binding.searchDataContainer.setScrollElevation(binding.incAppBar.root)
        binding.clearSearchHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        binding.incAppBar.incSearch.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                binding.searchDataContainer.visibility = View.VISIBLE
                binding.productsContainer.visibility = View.INVISIBLE
            }
        }


//        val mutableStateFlow = MutableStateFlow("")

//        binding.incAppBar.incSearch.etSearch.textChanges().debounce { 500 }.onEach {
//            viewModel.fetchMatchesQueries(it.toString())
//        }.launchIn(lifecycleScope)

        binding.incAppBar.incSearch.etSearch.doAfterTextChanged { query ->
            when (query?.trim().toString().isNotEmpty()) {
                true -> binding.incAppBar.incSearch.imgClear.visibility = View.VISIBLE
                false -> binding.incAppBar.incSearch.imgClear.visibility = View.INVISIBLE
            }
            viewModel.changeQuery(query.toString())
//            mutableStateFlow.value = query.toString()
        }


        binding.incAppBar.incSearch.imgClear.setOnClickListener {
            binding.incAppBar.incSearch.etSearch.setText("")
            binding.incAppBar.incSearch.etSearch.requestFocus()
            binding.searchDataContainer.visibility = View.VISIBLE
            binding.productsContainer.visibility = View.INVISIBLE
            binding.emptyResultContainer.isVisible = false
            viewModel.clearState()
        }

        binding.incAppBar.incSearch.etSearch.setOnEditorActionListener { _, actionId, _ ->

            val query = binding.incAppBar.incSearch.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    updateProductsByQuery(query)
                }
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateProductsByQuery(query)
                }
            }
            true
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

    private fun observeChangeLayoutManager() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel
                    .observeChangeLayoutManager()
                    .collect {
                        productsController.changeLayoutManager(
                            it,
                            binding.productRecycler,
                            binding.imgViewMode
                        )
                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeUiState()
                    .collect { state ->
                        bindHeader(state.data)

                        if (state.loadingPage) {
                            showLoader()
                        } else {
                            hideLoader()
                        }

                        bindShare(state.data.categoryHeader)

                        val data = state.data
                        if (state.bottomItem != null && state.data.layoutManager == FavoriteFlowViewModel.LINEAR) {
                            productsController.submitList(data.itemsList + state.bottomItem)
                        } else {
                            productsController.submitList(data.itemsList)
                        }

                        showError(state.error)

                    }
            }
        }
    }

    private fun bindHeader(state: SearchFlowViewModel.SearchState) {

        val categoryUiList = state.categoryHeader?.categoryUIList ?: emptyList()
        bindTabsVisibility(categoryUiList.isNotEmpty())
        categoryTabsController.submitList(categoryUiList, "")

        binding.tvCategoryName.text = state.categoryHeader?.productAmount

        binding.imgViewMode.setOnClickListener { viewModel.changeLayoutManager() }

        binding.tvSort.visibility = View.INVISIBLE
        state.categoryHeader?.sortTypeList?.let { sortTypeList ->
            binding.tvSort.setOnClickListener {
                showBottomSortSettings(
                    state.sortType,
                    sortTypeList
                )
            }
            binding.tvSort.text = state.sortType.sortName
            binding.tvSort.visibility = View.VISIBLE
        }

        binding.imgCategories.setOnClickListener {
            val category = state.categoryHeader ?: return@setOnClickListener
            val id = state.selectedCategoryId
            showMiniCatalog(category, id)
        }
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }

        if (state.categoryHeader != null) {
            showContainer(true)
        }

        val matches = state.matchesQuery

        if (state.popularCategoryDetail != null) {
            binding.bestForYouRv.visibility = View.VISIBLE
            val homeProducts = if (state.mayBeSearchDetail != null && state.mayBeSearchDetail.productUIList.isNotEmpty()) {
                HomeProducts(
                    1,
                    listOf(state.mayBeSearchDetail),
                    ProductsSliderConfig(
                        containShowAllButton = false,
                        largeTitle = true
                    ),
                    HomeProducts.DISCOUNT,
                    prodList = state.mayBeSearchDetail.productUIList
                )
            } else {
                HomeProducts(
                    1,
                    listOf(state.popularCategoryDetail),
                    ProductsSliderConfig(
                        containShowAllButton = false,
                        largeTitle = true
                    ),
                    HomeProducts.DISCOUNT,
                    prodList = state.popularCategoryDetail.productUIList
                )
            }
            val homeTitle = if (state.mayBeSearchDetail != null && state.mayBeSearchDetail.productUIList.isNotEmpty()) {
                HomeTitle(
                    id = 1,
                    type = HomeTitle.VIEWED_TITLE,
                    name = "Популярные товары",
                    showAll = false,
                    showAllName = "СМ.ВСЕ",
                    categoryProductsName = state.mayBeSearchDetail.name
                )
            } else {
                HomeTitle(
                    id = 1,
                    type = HomeTitle.VIEWED_TITLE,
                    name = "Популярные товары",
                    showAll = false,
                    showAllName = "СМ.ВСЕ",
                    categoryProductsName = state.popularCategoryDetail.name
                )
            }
            if(matches != null && (state.mayBeSearchDetail == null || state.mayBeSearchDetail.productUIList.isEmpty())){
                binding.bestForYouRv.visibility = View.GONE
            }
            bestForYouController.submitList(listOf(homeTitle, homeProducts))
            showContainer(false)
        } else {
            binding.bestForYouRv.visibility = View.GONE
        }


        if (matches == null) {
            binding.matchesQueriesContainer.visibility = View.GONE
            if (state.historyQuery.isEmpty()) {
                binding.historyQueryContainer.visibility = View.GONE
            } else {
                binding.historyQueryContainer.visibility = View.VISIBLE
                binding.historyQueryChipGroup.removeAllViews()
                state.historyQuery.forEach { query ->
                    binding.historyQueryChipGroup.addView(buildQueryChip(query))
                }
            }

            if (state.popularQuery.isEmpty()) {
                binding.popularQueryContainer.visibility = View.GONE
            } else {
                binding.popularQueryContainer.visibility = View.VISIBLE
                binding.popularQueryChipGroup.removeAllViews()
                state.popularQuery.forEach { query ->
                    binding.popularQueryChipGroup.addView(buildQueryChip(query))
                }
            }
        } else {
            binding.historyQueryContainer.visibility = View.GONE
            binding.popularQueryContainer.visibility = View.GONE
            binding.matchesQueriesContainer.visibility = View.VISIBLE
            binding.emptyResultContainer.isVisible = false

            if(matches.name.isNotEmpty()){
                binding.matchesQueriesTitle.visibility = View.VISIBLE
                binding.matchesQueriesTitle.text = matches.name
            } else {
                binding.matchesQueriesTitle.visibility = View.GONE
            }

            if (matches.quickQueryList.isNotEmpty()) {
                binding.matchesQueriesChipGroup.visibility = View.VISIBLE
                binding.matchesQueriesChipGroup.removeAllViews()
                matches.quickQueryList.forEach { query ->
                    binding.matchesQueriesChipGroup.addView(buildQueryChip(query))
                }
            } else {
                binding.matchesQueriesChipGroup.visibility = View.GONE
            }

            if(matches.errorText.isNotEmpty()){
                binding.matchesQueriesError.visibility = View.VISIBLE
                binding.matchesQueriesError.text = matches.errorText.fromHtml()
            } else {
                binding.matchesQueriesError.visibility = View.GONE
            }

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
        binding.incAppBar.incSearch.etSearch.setText(query)
        viewModel.updateQuery(query)

        val view = requireActivity().currentFocus
        if (view != null) {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun bindShare(categoryUI: CategoryUI?) {
        if (categoryUI == null) return

        if (categoryUI.shareUrl.isNotEmpty()) {
            binding.imgShare.isVisible = true
            binding.imgShare.setOnClickListener {
                val intent = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, categoryUI.shareUrl)
                    },
                    "Shearing Option"
                )
                startActivity(intent)
            }
        } else {
            binding.imgShare.isVisible = false
        }
    }

    private fun bindTabsVisibility(vis: Boolean) {
        when (vis) {
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
        /*binding.emptyFavoriteContainer.isVisible = !bool
        binding.favoriteContainer.isVisible = bool*/
    }

    private fun categoryTabsClickListener(): CategoryTabsFlowClickListener {
        return object : CategoryTabsFlowClickListener {
            override fun onTabClick(id: Long) {
                viewModel.onTabClick(id)
            }
        }
    }

    private fun showMiniCatalog(categoryUI: CategoryUI, id: Long) = findNavController().navigate(
        SearchFragmentDirections.actionToMiniCatalogBottomFragment(
            categoryUI.categoryUIList.toTypedArray(),
            id
        )
    )

    private fun showBottomSortSettings(
        sortType: SortTypeUI,
        sortTypeList: SortTypeListUI?,
    ) = sortTypeList?.let {
        findNavController().navigate(
            SearchFragmentDirections.actionToSortProductsSettingsBottomFragment(
                sortType,
                it
            )
        )
    }

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.onTabClick(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<SortTypeUI>(SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateBySortType(sortType)
            }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(
                    SearchFragmentDirections.actionToProductDetailFragment(
                        id
                    )
                )
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                viewModel.onPreOrderClick(id, name, detailPicture)
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {
                viewModel.changeRating(id, rating, oldRating)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.incAppBar.incSearch.etSearch.requestFocus()
    }

    private fun getProductsShowClickListener(): ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {}
            override fun showAllTopProducts(id: Long) {}
            override fun showAllNoveltiesProducts(id: Long) {}
            override fun showAllBottomProducts(id: Long) {}
        }
    }
}
