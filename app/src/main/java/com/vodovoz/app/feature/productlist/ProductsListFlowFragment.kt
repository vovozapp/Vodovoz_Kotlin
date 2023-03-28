package com.vodovoz.app.feature.productlist

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.FragmentMainFavoriteFlowBinding
import com.vodovoz.app.databinding.FragmentProductsFlowBinding
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFragmentDirections
import com.vodovoz.app.feature.favorite.FavoritesListController
import com.vodovoz.app.feature.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.feature.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductsListFlowFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_products_flow

    private val binding: FragmentProductsFlowBinding by viewBinding {
        FragmentProductsFlowBinding.bind(
            contentView
        )
    }

    private val viewModel: ProductsListFlowViewModel by viewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    private val space: Int by lazy { resources.getDimension(R.dimen.space_16).toInt() }

    private val categoryTabsController = CategoryTabsFlowController(categoryTabsClickListener())
    private val bestForYouController by lazy { BestForYouController(cartManager, likeManager, getProductsShowClickListener(), getProductsClickListener()) }
    private val favoritesController by lazy {
        ProductsListFlowController(viewModel, cartManager, likeManager, getProductsClickListener(), requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
        viewModel.firstLoadSorted()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryTabsController.bind(binding.brandRecycler, space)
        favoritesController.bind(binding.productRecycler, null)

        observeUiState()
        observeResultLiveData()
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
                    if (state.bottomItem != null && state.data.layoutManager == FavoriteFlowViewModel.LINEAR) {
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

    private fun bindHeader(state: ProductsListFlowViewModel.ProductsListState) {

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

        binding.tvSort.setOnClickListener { showBottomSortSettings(state.sortType) }
        binding.imgFilters.setOnClickListener {
            val category = state.favoriteCategory ?: return@setOnClickListener
            val id = state.selectedCategoryId ?: return@setOnClickListener
            showMiniCatalog(category, id)
        }


    }

    private fun bindTabsVisibility(vis: Boolean) {
        when(vis) {
            true -> {
                binding.brandRecycler.visibility = View.VISIBLE
                binding.imgFilters.visibility = View.VISIBLE
            }
            else -> {
                binding.imgFilters.visibility = View.GONE
                binding.brandRecycler.visibility = View.GONE
            }
        }
    }

    private fun showContainer(bool: Boolean) {
        binding.hiddenHeaderContainer.isVisible = !bool
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

}