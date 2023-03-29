package com.vodovoz.app.feature.productlistnofilter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.databinding.FragmentMainFavoriteFlowBinding
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.FragmentProductsFlowBinding
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.adapter.PagingProductsAdapter
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel.Companion.GRID
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel.Companion.LINEAR
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.feature.productlist.PaginatedProductsCatalogFragmentDirections
import com.vodovoz.app.feature.productlist.ProductsListFlowFragment
import com.vodovoz.app.feature.productlist.ProductsListFlowViewModel
import com.vodovoz.app.feature.productlist.brand.BrandFlowClickListener
import com.vodovoz.app.feature.productlist.brand.BrandFlowController
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ProductsListNoFilterFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_products_flow

    private val binding: FragmentProductsFlowBinding by viewBinding {
        FragmentProductsFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: ProductsListNoFilterFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private val brandController = BrandFlowController(brandClickListener())
    private val productsListNoFilterFlowController by lazy {
        ProductsListNoFilterFlowController(viewModel, cartManager, likeManager, getProductsClickListener(), requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        brandController.bind(binding.brandRecycler, space)
        productsListNoFilterFlowController.bind(binding.productRecycler, null)

        observeUiState()
        observeResultLiveData()
        observeChangeLayoutManager()
        initBackButton()
        initSearch()
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
                    productsListNoFilterFlowController.changeLayoutManager(it, binding.productRecycler, binding.imgViewMode)
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
                        binding.sortContainer.isVisible = true
                        binding.appBar.elevation = 4F
                    }

                    bindShare(state.data.categoryHeader)

                    val data = state.data
                    if (state.bottomItem != null && state.data.layoutManager == FavoriteFlowViewModel.LINEAR) {
                        productsListNoFilterFlowController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        productsListNoFilterFlowController.submitList(data.itemsList)
                    }

                    if (state.error !is ErrorState.Empty) {
                        showError(state.error)
                    }

                }
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

    private fun bindHeader(state: ProductsListNoFilterFlowViewModel.ProductListNoFilterState) {

        if (state.categoryHeader == null) return

        binding.imgViewMode.setOnClickListener { viewModel.changeLayoutManager() }
        binding.tvSort.setOnClickListener { showBottomSortSettings(state.sortType) }
        binding.imgFilters.setOnClickListener {
            val filterBundle = state.filterBundle
            val id = state.categoryId
            if (id == 0L) return@setOnClickListener
            showAllFiltersFragment(filterBundle, id)
        }

        binding.changeCategoryContainer.isVisible = state.showCatagoryContainer

        binding.categoryContainer.setOnClickListener {
            val id = state.categoryId
            if (id == 0L) return@setOnClickListener
            showSingleRootCatalogCatalog(id)
        }
        binding.incAppBar.imgBack.setOnClickListener { findNavController().popBackStack() }

        binding.tvSort.text = state.sortType.sortName

        when(state.filtersAmount) {
            0 -> binding.tvFiltersAmount.visibility = View.INVISIBLE
            else -> {
                binding.tvFiltersAmount.text = state.filtersAmount.toString()
                binding.tvFiltersAmount.visibility = View.VISIBLE
            }
        }


        binding.tvCategoryName.text = state.categoryHeader.name
        binding.tvProductAmount.text = state.categoryHeader.productAmount.toString()
        binding.additionalName.text = state.categoryHeader.primaryFilterName

        val brandList = state.categoryHeader.primaryFilterValueList

        when(brandList.isNotEmpty()) {
            true -> {
                binding.brandTabsContainer.visibility = View.VISIBLE
            }
            else -> {
                binding.brandTabsContainer.visibility = View.GONE
            }
        }
        brandController.submitList(brandList)
    }

    private fun brandClickListener() : BrandFlowClickListener {
        return object : BrandFlowClickListener {
            override fun onBrandClick(filterValue: FilterValueUI) {
                viewModel.addPrimaryFilterValue(filterValue)
            }

        }
    }

    private fun showAllFiltersFragment(filterBundle: FiltersBundleUI, id: Long) = findNavController().navigate(
        PaginatedProductsCatalogFragmentDirections.actionToProductFiltersFragment(filterBundle, id)
    )

    private fun showSingleRootCatalogCatalog(id: Long) = findNavController().navigate(
        PaginatedProductsCatalogFragmentDirections.actionToSingleRootCatalogBottomFragment(id)
    )

    private fun showBottomSortSettings(sortType: SortType) = findNavController().navigate(
        PaginatedProductsCatalogFragmentDirections.actionToSortProductsSettingsBottomFragment(sortType.name)
    )

    private fun observeResultLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(ProductsListFlowFragment.CATEGORY_ID)?.observe(viewLifecycleOwner) { categoryId ->
                viewModel.updateByCat(categoryId)
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<String>(ProductsListFlowFragment.SORT_TYPE)?.observe(viewLifecycleOwner) { sortType ->
                viewModel.updateBySortType(SortType.valueOf(sortType))
            }

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<FiltersBundleUI>(ProductsListFlowFragment.FILTER_BUNDLE)?.observe(viewLifecycleOwner) { filterBundle ->
                viewModel.updateFilterBundle(filterBundle)
            }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(PaginatedProductsCatalogFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                findNavController().navigate(
                    PaginatedProductsCatalogFragmentDirections.actionToPreOrderBS(
                        id,
                        name,
                        detailPicture
                    )
                )
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                viewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                viewModel.changeFavoriteStatus(id, isFavorite)
            }

        }
    }

}