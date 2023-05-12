package com.vodovoz.app.feature.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentMainHomeFlowBinding
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.catalog.CatalogFragmentDirections
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.popup.NewsClickListener
import com.vodovoz.app.feature.home.popup.PopupNewsBottomFragment
import com.vodovoz.app.feature.home.ratebottom.RateBottomFragment
import com.vodovoz.app.feature.home.ratebottom.RateBottomViewModel
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.onlyproducts.ProductsCatalogFragment
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.util.extensions.addOnBackPressedCallback
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_main_home_flow
    override fun update() {
        flowViewModel.refresh()
    }

    private val binding: FragmentMainHomeFlowBinding by viewBinding {
        FragmentMainHomeFlowBinding.bind(
            contentView
        )
    }

    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val rateBottomViewModel: RateBottomViewModel by activityViewModels()

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var siteStateManager: SiteStateManager

    private val homeController by lazy {
        HomeController(
            viewModel = flowViewModel,
            cartManager = cartManager,
            likeManager = likeManager,
            listener = getMainClickListener(),
            productsShowAllListener = getProductsShowClickListener(),
            productsClickListener = getProductsClickListener(),
            promotionsClickListener = getPromotionsClickListener()
        ) {
            if (siteStateManager.showRateBottom != null) {
                if (!siteStateManager.showRateBottom!!) {
                    siteStateManager.showRateBottom = true
                    RateBottomFragment().show(childFragmentManager, "TAG")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flowViewModel.firstLoad()
        rateBottomViewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeController.bind(binding.homeRv, binding.refreshContainer)
        initSearchToolbar(
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) }
        )
        bindErrorRefresh { flowViewModel.refresh() }
        observeUiState()
        observeTabReselect()
        observeEvents()
        bindBackPressed()
        observeSiteState()
    }

    private fun observeSiteState() {
        lifecycleScope.launchWhenStarted {
            siteStateManager
                .observeDeepLinkPath()
                .collect {
                    when(it) {
                        "catalog" -> {
                            tabManager.selectTab(R.id.graph_catalog)
                            siteStateManager.clearDeepLinkListener()
                        }
                        "action" -> {
                            findNavController().navigate(
                                HomeFragmentDirections.actionToAllPromotionsFragment(
                                    AllPromotionsFragment.DataSource.All()
                                )
                            )
                            siteStateManager.clearDeepLinkListener()
                        }
                        "brand" -> {
                            findNavController().navigate(HomeFragmentDirections.actionToAllBrandsFragment())
                            siteStateManager.clearDeepLinkListener()
                        }
                        "mobile_app" -> {
                            findNavController().navigate(HomeFragmentDirections.actionToAboutAppDialogFragment())
                            siteStateManager.clearDeepLinkListener()
                        }
                        "about" -> {
                            findNavController().navigate(
                                HomeFragmentDirections.actionToWebViewFragment(
                                    ApiConfig.ABOUT_SHOP_URL,
                                    "О магазине"
                                )
                            )
                            siteStateManager.clearDeepLinkListener()
                        }
                        "dostavka" -> {
                            findNavController().navigate(
                                HomeFragmentDirections.actionToWebViewFragment(
                                    ApiConfig.ABOUT_DELIVERY_URL,
                                    "О доставке"
                                )
                            )
                            siteStateManager.clearDeepLinkListener()
                        }
                        "service" -> {
                            findNavController().navigate(HomeFragmentDirections.actionToAboutServicesDialogFragment())
                            siteStateManager.clearDeepLinkListener()
                        }
                        "remont_kulerov" -> {
                            findNavController().navigate(HomeFragmentDirections.actionToAboutServicesDialogFragment())
                            siteStateManager.clearDeepLinkListener()
                        }
                        "feedback" -> {
                            findNavController().navigate(HomeFragmentDirections.actionToContactsFragment())
                            siteStateManager.clearDeepLinkListener()
                        }
                        "gl" -> {
                            siteStateManager.clearDeepLinkListener()
                        }
                        null -> {
                            siteStateManager.clearDeepLinkListener()
                        }
                    }
                }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launchWhenStarted {
            flowViewModel.observeEvent()
                .collect {
                    when(it) {
                        is HomeFlowViewModel.HomeEvents.GoToPreOrder -> {
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.preOrderBS) {
                                findNavController().popBackStack()
                            }
                            findNavController().navigate(
                                HomeFragmentDirections.actionToPreOrderBS(
                                    it.id,
                                    it.name,
                                    it.detailPicture
                                )
                            )
                        }
                        is HomeFlowViewModel.HomeEvents.GoToProfile -> {
                            tabManager.setAuthRedirect(findNavController().graph.id)
                            tabManager.selectTab(R.id.graph_profile)
                        }
                        is HomeFlowViewModel.HomeEvents.SendComment -> {
                            findNavController().navigate(HomeFragmentDirections.actionToSendCommentAboutShopBottomDialog())
                        }
                    }
                }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            flowViewModel.observeUiState()
                .collect { homeState ->

                    if (homeState.data.items.mapNotNull { it.item }.size < 8) {
                        binding.homeRv.isVisible = false
                        showLoaderWithBg(true)
                    } else {
                        binding.homeRv.isVisible = true
                        showLoaderWithBg(false)
                    }

                    if (homeState.data.news != null && !homeState.data.hasShow) {
                        showPopUpNews(homeState.data.news)
                    }

                    if (homeState.data.items.size in (HomeFlowViewModel.POSITIONS_COUNT - 8..HomeFlowViewModel.POSITIONS_COUNT)) {
                        val list =
                            homeState.data.items.sortedBy { it.position }.mapNotNull { it.item }
                        homeController.submitList(list)
                    }

                    showError(homeState.error)

                }
        }
    }

    private fun getProductsClickListener(): ProductsClickListener {
        return object : ProductsClickListener {
            override fun onProductClick(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                flowViewModel.onPreOrderClick(id, name, detailPicture)
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int) {
                flowViewModel.changeCart(id, cartQuantity, oldQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                flowViewModel.changeFavoriteStatus(id, isFavorite)
            }

            override fun onChangeRating(id: Long, rating: Float, oldRating: Float) {
                flowViewModel.changeRating(id, rating, oldRating)
            }
        }
    }

    private fun getProductsShowClickListener(): ProductsShowAllListener {
        return object : ProductsShowAllListener {
            override fun showAllDiscountProducts(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
                    )
                )
            }

            override fun showAllTopProducts(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(id)
                    )
                )
            }

            override fun showAllNoveltiesProducts(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
                    )
                )
            }

            override fun showAllBottomProducts(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(id)
                    )
                )
            }
        }
    }

    private fun getPromotionsClickListener(): PromotionsClickListener {
        return object : PromotionsClickListener {
            override fun onPromotionClick(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPromotionDetailFragment(
                        id
                    )
                )
            }

            override fun onShowAllPromotionsClick() {
                findNavController().navigate(
                    HomeFragmentDirections.actionToAllPromotionsFragment(
                        AllPromotionsFragment.DataSource.All()
                    )
                )
            }
        }
    }


    private fun getMainClickListener(): HomeMainClickListener {
        return object : HomeMainClickListener {

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
                    )
                )
            }

            override fun onAboutPayClick() {
                findNavController().navigate(
                    HomeFragmentDirections.actionToWebViewFragment(
                        ApiConfig.ABOUT_PAY_URL,
                        "Об оплате"
                    )
                )
            }

            override fun onAboutShopClick() {
                findNavController().navigate(
                    HomeFragmentDirections.actionToWebViewFragment(
                        ApiConfig.ABOUT_SHOP_URL,
                        "О магазине"
                    )
                )
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
                    )
                )
            }

            override fun onShowAllBrandsClick() {
                findNavController().navigate(HomeFragmentDirections.actionToAllBrandsFragment())
            }

            //POSITION_15
            override fun onCommentsClick(id: Long?) {}

            override fun onSendCommentAboutShop() {
                flowViewModel.onSendCommentClick()
            }

            //POSITION_13
            override fun onCountryClick(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Country(id)
                    )
                )
            }

            //POSITION_2
            override fun onHistoryClick(id: Long) {
                findNavController().navigate(
                    HomeFragmentDirections.actionToFullScreenHistorySliderFragment(
                        id
                    )
                )
            }

            //POSITION_7
            override fun onOrderClick(id: Long?) {
                id?.let {
                    findNavController().navigate(
                        HomeFragmentDirections.actionToOrderDetailsFragment(
                            id
                        )
                    )
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
                tabManager.selectTab(R.id.graph_favorite)
            }

            //POSITION_3
            override fun onCategoryClick(id: Long?) {
                id?.let {
                    findNavController().navigate(
                        HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(
                            id
                        )
                    )
                }
            }
        }
    }

    private fun ActionEntity.invoke(
        navController: NavController = findNavController(),
        activity: FragmentActivity = requireActivity()
    ) {
        val navDirect = when (this) {
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

    private fun showPopUpNews(data: PopupNewsUI) {
        val dialog = PopupNewsBottomFragment.newInstance(
            data,
            clickListener = newsClickListener()
        )

        dialog.show(childFragmentManager, dialog::class.simpleName)
        flowViewModel.hasShown()
    }

    private fun newsClickListener() : NewsClickListener {
        return object : NewsClickListener {
            override fun onClick(actionEntity: ActionEntity) {
                actionEntity.invoke()
            }
        }
    }

    private fun observeTabReselect() {
        lifecycleScope.launchWhenStarted {
            tabManager.observeTabReselect()
                .collect {
                    if (it != TabManager.DEFAULT_STATE && it == R.id.homeFragment) {
                        binding.homeRv.post {
                            binding.homeRv.smoothScrollToPosition(0)
                        }
                        tabManager.setDefaultState()
                    }
                }
        }
    }

    private fun bindBackPressed() {
        var back = false
        addOnBackPressedCallback {
            if (!back) {
                requireActivity().snack("Нажмите назад еще раз, чтобы выйти") {}
                back = true
            } else {
                requireActivity().finish()
            }
        }
    }
}