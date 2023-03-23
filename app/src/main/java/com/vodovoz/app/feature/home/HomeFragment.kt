package com.vodovoz.app.feature.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentMainHomeFlowBinding
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts.ProductsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.ui.fragment.all_promotions.AllPromotionsFragment
import com.vodovoz.app.ui.fragment.catalog.CatalogFragmentDirections
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.products_catalog.ProductsCatalogFragment
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_main_home_flow
    override fun update() { flowViewModel.refresh() }

    private val binding: FragmentMainHomeFlowBinding by viewBinding { FragmentMainHomeFlowBinding.bind(contentView) }

    private val flowViewModel: HomeFlowViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var tabManager: TabManager

    private val homeController by lazy {
        HomeController(
            viewModel = flowViewModel,
            cartManager = cartManager,
            likeManager = likeManager,
            listener = getMainClickListener(),
            productsShowAllListener = getProductsShowClickListener(),
            productsClickListener = getProductsClickListener(),
            promotionsClickListener = getPromotionsClickListener()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flowViewModel.firstLoad()
        observeUiState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindButtons()
        homeController.bind(binding.homeRv, binding.refreshContainer)
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            flowViewModel.observeUiState()
                .collect { homeState ->

                    if (homeState.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    if (homeState.data.items.size in (HomeFlowViewModel.POSITIONS_COUNT - 2..HomeFlowViewModel.POSITIONS_COUNT)) {
                        val list =
                            homeState.data.items.sortedBy { it.position }.mapNotNull { it.item }
                        homeController.submitList(list)
                    }

                    debugLog { "${homeState.data.items.map { it.position }.sorted()}" }
                }
        }
    }

    private fun bindButtons() {

        binding.searchContainer.clSearchContainer.setOnClickListener {
            findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment())
        }
        binding.searchContainer.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment())
            }
        }
    }

    private fun getProductsClickListener() : ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                showPreOrderProductPopup(id, name, detailPicture)
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                flowViewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                flowViewModel.changeFavoriteStatus(id, isFavorite)
            }

        }
    }
    private fun getProductsShowClickListener() : ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
                    ))
            }

            override fun showAllTopProducts(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(id)
                    ))
            }

            override fun showAllNoveltiesProducts(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
                    ))
            }

            override fun showAllBottomProducts(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(id)
                    ))
            }
        }
    }

    private fun getPromotionsClickListener() : PromotionsClickListener {
        return object : PromotionsClickListener {
            override fun onPromotionClick(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToPromotionDetailFragment(id))
            }

            override fun onShowAllPromotionsClick() {
                findNavController().navigate(
                    HomeFragmentDirections.actionToAllPromotionsFragment(
                        AllPromotionsFragment.DataSource.All()
                    ))
            }
        }
    }


    private fun getMainClickListener() : HomeMainClickListener {
        return object: HomeMainClickListener {

            //POSITION_1
            override fun onBannerClick(actionEntity: ActionEntity?) {
                actionEntity?.invoke(findNavController(), requireActivity())
            }

            //POSITION_16
            override fun onAboutAppClick() {
                findNavController().navigate(HomeFragmentDirections.actionToAboutAppDialogFragment())
            }

            override fun onAboutDeliveryClick() {
                findNavController().navigate(
                    HomeFragmentDirections.actionToWebViewFragment(
                        ApiConfig.ABOUT_DELIVERY_URL,
                        "О доставке"
                    ))
            }

            override fun onAboutPayClick() {
                findNavController().navigate(
                    HomeFragmentDirections.actionToWebViewFragment(
                        ApiConfig.ABOUT_PAY_URL,
                        "Об оплате"
                    ))
            }

            override fun onAboutShopClick() {
                findNavController().navigate(
                    HomeFragmentDirections.actionToWebViewFragment(
                        ApiConfig.ABOUT_SHOP_URL,
                        "О магазине"
                    ))
            }

            override fun onServicesClick() {
                findNavController().navigate(HomeFragmentDirections.actionToAboutServicesDialogFragment())
            }

            override fun onContactsClick() {
                findNavController().navigate(HomeFragmentDirections.actionToContactsFragment())
            }

            override fun onHowToOrderClick() {
                findNavController().navigate(HomeFragmentDirections.actionToHowToOrderFragment())
            }

            //POSITION_12
            override fun onBrandClick(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(id)
                    ))
            }

            override fun onShowAllBrandsClick() {
                findNavController().navigate(HomeFragmentDirections.actionToAllBrandsFragment())
            }

            //POSITION_15
            override fun onCommentsClick(id: Long?) {}

            override fun onSendCommentAboutShop() {
                if (flowViewModel.isLoginAlready()) {
                    findNavController().navigate(HomeFragmentDirections.actionToSendCommentAboutShopBottomDialog())
                } else {
                    findNavController().navigate(R.id.profileFragment)
                }
            }

            //POSITION_13
            override fun onCountryClick(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Country(id)
                    ))
            }

            //POSITION_2
            override fun onHistoryClick(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToFullScreenHistorySliderFragment(id))
            }

            //POSITION_7
            override fun onOrderClick(id: Long?) {
                id?.let {
                    findNavController().navigate(HomeFragmentDirections.actionToOrderDetailsFragment(id))
                }
            }

            override fun onShowAllOrdersClick() {
                findNavController().navigate(HomeFragmentDirections.actionToAllOrdersFragment())
            }

            //POSITION_8
            override fun onLastPurchasesClick() {
                findNavController().navigate(HomeFragmentDirections.actionToPastPurchasesFragment())
            }

            override fun onOrdersHistoryClick() {
                findNavController().navigate(HomeFragmentDirections.actionToAllOrdersFragment())
            }

            override fun onShowAllFavoritesClick() {
                tabManager.selectTab(R.id.favoriteFragment)
            }

            //POSITION_3
            override fun onCategoryClick(id: Long?) {
                id?.let {
                    findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(id))
                }
            }
        }
    }

    private fun ActionEntity.invoke(navController: NavController = findNavController(), activity: FragmentActivity = requireActivity())  {
        val navDirect = when(this) {
            is ActionEntity.Brand ->
                HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId = this.brandId)
                )
            is ActionEntity.Brands -> {
                HomeFragmentDirections.actionToAllBrandsFragment(this.brandIdList.toLongArray())
            }
            is ActionEntity.Product ->
                HomeFragmentDirections.actionToProductDetailFragment(this.productId)
            is ActionEntity.Products ->
                HomeFragmentDirections.actionToProductsCatalogFragment(
                    ProductsCatalogFragment.DataSource.BannerProducts(categoryId = this.categoryId)
                )
            is ActionEntity.Promotion ->
                HomeFragmentDirections.actionToPromotionDetailFragment(this.promotionId)
            is ActionEntity.Promotions -> HomeFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.ByBanner(this.categoryId)
            )
            is ActionEntity.AllPromotions -> HomeFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.All()
            )
            is ActionEntity.Link -> {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.url))
                activity.startActivity(openLinkIntent)
                null
            }
            is ActionEntity.Category ->
                HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(this.categoryId)
            is ActionEntity.Discount -> HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
            )
            is ActionEntity.Novelties -> HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
            )
        }
        navDirect?.let { navController.navigate(navDirect) }
    }

    private fun showPreOrderProductPopup(id: Long, name: String, picture: String) {
        when(flowViewModel.isLoginAlready()) {
            true -> findNavController().navigate(HomeFragmentDirections.actionToPreOrderBS(id, name, picture))
            false -> findNavController().navigate(HomeFragmentDirections.actionToProfileFragment())
        }
    }
}