package com.vodovoz.app.ui.fragment.favorite

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.databinding.FragmentMainFavoriteFlowBinding
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.ui.adapter.PagingProductsAdapter
import com.vodovoz.app.ui.fragment.favorite.FavoriteFlowViewModel.Companion.GRID
import com.vodovoz.app.ui.fragment.favorite.FavoriteFlowViewModel.Companion.LINEAR
import com.vodovoz.app.ui.fragment.favorite.bestforyouadapter.BestForYouController
import com.vodovoz.app.ui.fragment.favorite.categorytabsdadapter.CategoryTabsFlowClickListener
import com.vodovoz.app.ui.fragment.favorite.categorytabsdadapter.CategoryTabsFlowController
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFlowFragment : BaseFragment() {

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
                    if (state.bottomItem != null) {
                        favoritesController.submitList(data.itemsList + state.bottomItem)
                    } else {
                        favoritesController.submitList(data.itemsList)
                    }

                    showError(state.error)

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
                DISCOUNT
            )
            bestForYouController.submitList(listOf(homeProducts))
            showContainer(false)
        }

        val categoryUiList = state.favoriteCategory?.categoryUIList ?: emptyList()

        when(categoryUiList.isNotEmpty()) {
            true -> {
                binding.categoriesRecycler.visibility = View.VISIBLE
                binding.imgCategories.visibility = View.VISIBLE
            }
            else -> {
                binding.imgCategories.visibility = View.GONE
                binding.categoriesRecycler.visibility = View.GONE
            }
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
            val id = state.favoriteCategory.id ?: return@setOnClickListener
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