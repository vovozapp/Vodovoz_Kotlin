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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.chip.Chip
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.FragmentSearchFlowBinding
import com.vodovoz.app.databinding.ViewSimpleTextChipBinding
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFragmentDirections
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.fragment.search.SearchFragment
import com.vodovoz.app.ui.fragment.search.SearchFragmentArgs
import com.vodovoz.app.ui.fragment.search.SearchFragmentDirections
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SearchFlowFragment : BaseFragment() {

    companion object {
        const val CATEGORY_ID = "CATEGORY_ID"
        const val SORT_TYPE = "SORT_TYPE"
    }

    override fun layout(): Int = R.layout.fragment_search_flow

    private val space8 by lazy { resources.getDimension(R.dimen.space_8) }

    private val binding: FragmentSearchFlowBinding by viewBinding {
        FragmentSearchFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: SearchFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }
    private val compositeDisposable = CompositeDisposable()

    private val categoryTabsController = CategoryTabsFlowController(categoryTabsClickListener())
    private val bestForYouController by lazy { BestForYouController(cartManager, likeManager, getProductsShowClickListener(), getProductsClickListener()) }
    private val productsController by lazy {
        SearchFlowController(viewModel, cartManager, likeManager, getProductsClickListener(), requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryTabsController.bind(binding.categoriesRecycler, space)
        bestForYouController.bind(binding.bestForYouRv)
        productsController.bind(binding.productRecycler, null)

        observeUiState()
        observeResultLiveData()
        observeChangeLayoutManager()
        initBackButton()
        initSearch()
    }

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

        Observable.create<String> { emitter ->
            binding.incAppBar.incSearch.etSearch.doAfterTextChanged { query ->
                when(query?.trim().toString().isNotEmpty()) {
                    true ->  binding.incAppBar.incSearch.imgClear.visibility = View.VISIBLE
                    false ->binding.incAppBar.incSearch.imgClear.visibility = View.INVISIBLE
                }
                emitter.onNext(query.toString())
            }
        }.debounce(300, TimeUnit.MILLISECONDS).subscribeBy { query ->
            viewModel.fetchMatchesQueries(query)
        }.addTo(compositeDisposable)

        binding.incAppBar.incSearch.imgClear.setOnClickListener {
            binding.incAppBar.incSearch.etSearch.setText("")
            binding.incAppBar.incSearch.etSearch.requestFocus()
            binding.searchDataContainer.visibility = View.VISIBLE
            binding.productsContainer.visibility = View.INVISIBLE
            viewModel.clearState()
        }

        binding.incAppBar.incSearch.etSearch.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.incAppBar.incSearch.etSearch.text.toString().trim()
                if (query.isNotEmpty()) {
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
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeChangeLayoutManager()
                .collect {
                    productsController.changeLayoutManager(it, binding.productRecycler, binding.imgViewMode)
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

                    bindShare(state.data.categoryHeader)

                    val data = state.data
                    if (state.bottomItem != null && state.data.layoutManager == FavoriteFlowViewModel.LINEAR && state.page != 2) {
                        productsController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        productsController.submitList(data.itemsList)
                    }

                    if (state.error !is ErrorState.Empty) {
                        showError(state.error)
                    }

                }
        }
    }

    private fun bindHeader(state: SearchFlowViewModel.SearchState) {

        val categoryUiList = state.categoryHeader?.categoryUIList ?: emptyList()
        bindTabsVisibility(categoryUiList.isNotEmpty())
        categoryTabsController.submitList(categoryUiList)

        binding.tvCategoryName.text = state.categoryHeader?.productAmount

        binding.imgViewMode.setOnClickListener { viewModel.changeLayoutManager() }
        binding.tvSort.setOnClickListener { showBottomSortSettings(state.sortType) }
        binding.imgCategories.setOnClickListener {
            val category = state.categoryHeader ?: return@setOnClickListener
            val id = state.selectedCategoryId
            showMiniCatalog(category, id)
        }
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }
        binding.tvSort.text = state.sortType.sortName

        if (state.categoryHeader != null) {
            showContainer(true)
        }

        if (state.popularCategoryDetail != null) {
            val homeProducts = HomeProducts(
                1,
                listOf(state.popularCategoryDetail),
                ProductsSliderConfig(
                    containShowAllButton = false,
                    largeTitle = true
                ),
                HomeProducts.DISCOUNT
            )
            bestForYouController.submitList(listOf(homeProducts))
            showContainer(false)
        }

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

        SearchFragmentArgs.fromBundle(requireArguments()).query.let { query ->
            if (query.isNotEmpty()) {
                updateProductsByQuery(query)
            }
        }

        if (state.matchesQuery.isEmpty()) {
            binding.matchesQueriesContainer.visibility = View.GONE
        } else {
            binding.matchesQueriesContainer.visibility = View.VISIBLE
            binding.matchesQueriesChipGroup.removeAllViews()
            state.matchesQuery.forEach { query ->
                binding.matchesQueriesChipGroup.addView(buildQueryChip(query))
            }
        }

    }

    private fun buildQueryChip(query: String): Chip {
        val chip = ViewSimpleTextChipBinding.inflate(layoutInflater, null, false).root
        chip.chipCornerRadius = space8
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
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
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
        /*binding.emptyFavoriteContainer.isVisible = !bool
        binding.favoriteContainer.isVisible = bool*/
    }

    private fun categoryTabsClickListener() : CategoryTabsFlowClickListener {
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

    private fun showBottomSortSettings(sortType: SortType) = findNavController().navigate(
        SearchFragmentDirections.actionToSortProductsSettingsBottomFragment(sortType.name))

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(SearchFragment.CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.onTabClick(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(SearchFragment.SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateBySortType(SortType.valueOf(sortType))
            }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(SearchFragmentDirections.actionToProductDetailFragment(id))
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
                    false -> findNavController().navigate(SearchFragmentDirections.actionToProfileFragment())
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

    override fun onResume() {
        super.onResume()
        binding.incAppBar.incSearch.etSearch.requestFocus()
    }

    private fun getProductsShowClickListener() : ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {}
            override fun showAllTopProducts(id: Long) {}
            override fun showAllNoveltiesProducts(id: Long) {}
            override fun showAllBottomProducts(id: Long) {}
        }
    }

}