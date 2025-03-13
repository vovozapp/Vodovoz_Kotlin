package com.vodovoz.app.feature.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.media.MediaManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.android.activate
import com.vodovoz.app.core.android.createDataAllActivator
import com.vodovoz.app.core.navigation.navigateToProductDetails
import com.vodovoz.app.core.navigation.navigateToPromotionDetails
import com.vodovoz.app.core.navigation.navigateToSearch
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.domain.general.model.DataAllAction
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.home.popup.NewsClickListener
import com.vodovoz.app.feature.onlyproducts.ProductsCatalogFragment
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val flowViewModel: HomeFlowViewModel by activityViewModels()

    @Inject
    lateinit var ratingProductManager: RatingProductManager

    @Inject
    lateinit var cartManager: CartManager

    @Inject
    lateinit var likeManager: LikeManager

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var siteStateManager: SiteStateManager

    @Inject
    lateinit var mediaManager: MediaManager

    @Inject
    lateinit var accountManager: AccountManager

    @Inject
    lateinit var cookieManager: com.vodovoz.app.common.cookie.CookieManager

    @Inject
    lateinit var permissionsControllerFactory: PermissionsController.Factory
    private val permissionsController by lazy { permissionsControllerFactory.create(requireActivity()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeMediaManager()
        observePushFromSiteState()
        observeDeepLinkFromSiteState()
        observeTabReselect()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {


                VodovozTheme {
                    val viewState by flowViewModel.observeUiState().collectAsStateWithLifecycle()
                    val topProductLazyListState = rememberLazyListState()

                    when (viewState.data.uiState) {
                        HomeFlowViewModel.HomeUiState.NetworkError -> {
                            NetworkErrorPlaceholder(onTryAgainClick = { flowViewModel.refresh() })
                        }

                        else -> {
                            HomeScreen(
                                viewState = viewState.data,
                                viewModel = flowViewModel,
                                topProductsLazyListState = topProductLazyListState,
                                onNavigateToQrCodeFragment = {
                                    navigateToQrCodeFragment()
                                }
                            )
                        }
                    }

                    LifecycleEffect {
                        observeEvents(topProductLazyListState = topProductLazyListState)
                    }

                }
            }
        }
    }

    private fun observeTabReselect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tabManager.observeTabReselect()
                    .collect {
                        if (it != TabManager.DEFAULT_STATE && it == R.id.homeFragment) {
                            tabManager.setDefaultState()
                        }
                    }
            }
        }
    }

    private fun newsClickListener(): NewsClickListener {
        return object : NewsClickListener {
            override fun onClick(actionEntity: ActionEntity) {
                if (actionEntity is ActionEntity.WaterApp) {
                    val eventParameters = "\"source\":\"bottom_alert\""
                    accountManager.reportEvent("trekervodi_zapysk", eventParameters)
                }
                actionEntity.activate()
            }
        }
    }

    private fun navigateToQrCodeFragment() {
        permissionsController.methodRequiresCameraPermission {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@methodRequiresCameraPermission
            }

            findNavController().navigate(R.id.qrCodeFragment)

        }
    }


    internal fun ActionEntity.activate(
        navController: NavController = findNavController(),
        activity: FragmentActivity = requireActivity(),
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
                AllPromotionsFragment.DataSource.All
            )

            is ActionEntity.Link -> {
                val openLinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.url))
                activity.startActivity(openLinkIntent)
                null
            }

            is ActionEntity.LinkWithCookies -> {
                setCookie()
                HomeFragmentDirections.actionToWebViewFragment(
                    url,
                    "",
                )
                null
            }

            is ActionEntity.Category ->
                HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(this.categoryId)

            is ActionEntity.Discount -> HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.HurryBuyUpProducts
            )

            is ActionEntity.Novelties -> HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.NewProducts
            )

            is ActionEntity.WaterApp -> {
                HomeFragmentDirections.actionToWaterAppFragment()
            }

            is ActionEntity.Delivery -> HomeFragmentDirections.actionToWebViewFragment(
                ApiConfig.ABOUT_DELIVERY_URL,
                "О доставке"
            )

            is ActionEntity.Profile -> {
                flowViewModel.goToProfile()
                null
            }

            is ActionEntity.BuyCertificate -> {
                HomeFragmentDirections.actionToBuyCertificateFragment()
            }
        }
        navDirect?.let { navController.navigate(navDirect) }
    }


    private suspend fun observeEvents(topProductLazyListState: LazyListState) {
        flowViewModel.observeEvent().collect { event ->
            when (event) {
                is HomeFlowViewModel.HomeEvents.GoToPreOrder -> {
                    if (findNavController().currentBackStackEntry?.destination?.id == R.id.preOrderBS) {
                        findNavController().popBackStack()
                    }


                    findNavController().navigate(
                        HomeFragmentDirections.actionToPreOrderBS(
                            event.id,
                            event.name,
                            event.detailPicture
                        )
                    )
                }

                is HomeFlowViewModel.HomeEvents.GoToProfile -> {
                    tabManager.setAuthRedirect(findNavController().graph.id)
                    tabManager.selectTab(R.id.graph_profile)
                }

                is HomeFlowViewModel.HomeEvents.SendComment -> {
                    if (findNavController().currentBackStackEntry?.destination?.id == R.id.sendCommentAboutShopBottomDialog) {
                        findNavController().popBackStack()
                    }
                    findNavController().navigate(HomeFragmentDirections.actionToSendCommentAboutShopBottomDialog())
                }

                is HomeFlowViewModel.HomeEvents.GoToCart -> {

                }

                is HomeFlowViewModel.HomeEvents.GoToStories -> {
                    val bundle = bundleOf("startHistoryId" to event.storyId)
                    findNavController().navigate(
                        R.id.fullScreenHistorySliderFragment,
                        bundle
                    )
                }

                is HomeFlowViewModel.HomeEvents.GoToProductDetails -> {
                    findNavController().navigateToProductDetails(event.productId)
                }

                is HomeFlowViewModel.HomeEvents.GoToPromotionDetails -> {
                    findNavController().navigateToPromotionDetails(event.promotionId)
                }

                is HomeFlowViewModel.HomeEvents.ActivateButtonAction -> {
                    event.action.activate(
                        navController = findNavController(),
                        activators = listOf(
                            createDataAllActivator(DataAllAction.Profile) {
                                tabManager.setAuthRedirect(findNavController().graph.id)
                                tabManager.selectTab(R.id.graph_profile)
                            },
                            createDataAllActivator(DataAllAction.Unknown) {
                                //TODO("Implement snackbar")
                            },
                        ),
                        activateIdAction = { id ->
                            findNavController().navigate(
                                HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.ButtonProducts(
                                        id
                                    )
                                )
                            )
                        }
                    )
                }

                HomeFlowViewModel.HomeEvents.GoToSearch -> {
                    findNavController().navigateToSearch()
                }

                HomeFlowViewModel.HomeEvents.ScrollTopProductsToStart -> {
                    topProductLazyListState.animateScrollToItem(0)
                }
            }
        }
    }


    private fun observeDeepLinkFromSiteState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                siteStateManager
                    .observeDeepLinkPath()
                    .collect { path ->
                        when (path) {
                            "mobile_app/" -> {
                                findNavController().navigate(HomeFragmentDirections.actionToAboutAppDialogFragment())
                            }

                            "gl/" -> {
                            }

                            "kalkulyator_vody/" -> {
                                val eventName = "trekervodi_ssilka"
                                accountManager.reportEvent(eventName)
                                findNavController().navigate(HomeFragmentDirections.actionToWaterAppFragment())
                            }
                        }

                        siteStateManager.clearDeepLinkListener()
                    }
            }
        }
    }


    private fun observeMediaManager() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mediaManager
                    .observeCommentData()
                    .collect {
                        if (it != null && it.show) {
                            mediaManager.dontShow()
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.sendCommentAboutProductFragment) {
                                findNavController().popBackStack()
                            }
                            findNavController().navigate(
                                HomeFragmentDirections.actionToSendCommentAboutProductFragment(
                                    it.productId,
                                    it.rate
                                )
                            )
                        }
                    }
            }
        }
    }

    private fun observePushFromSiteState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                siteStateManager
                    .observePush()
                    .collect {
                        debugLog { "push ${it?.path} $siteStateManager" }
                        when (it?.path) {
                            "AKCII" -> {
                                val promotionId = it.id
                                if (!promotionId.isNullOrEmpty()) {
                                    val eventParameters = "\"ID_AKCII\": \"$promotionId\""
                                    accountManager.reportEvent(
                                        "Зашел в акцию (push)",
                                        eventParameters
                                    )

                                    findNavController().navigate(
                                        HomeFragmentDirections.actionToPromotionDetailFragment(
                                            promotionId.toLong()
                                        )
                                    )
                                }
                            }

                            "TOVAR" -> {
                                val productId = it.id
                                if (!productId.isNullOrEmpty()) {
                                    val eventParameters = "\"ID_Product\": \"$productId\""
                                    accountManager.reportEvent(
                                        "Зашел в товар (push)",
                                        eventParameters
                                    )

                                    findNavController().navigate(
                                        HomeFragmentDirections.actionToProductDetailFragment(
                                            productId.toLong()
                                        )
                                    )
                                }
                            }

                            "RAZDEL" -> {
                                val sectionId = it.id
                                if (!sectionId.isNullOrEmpty()) {
                                    val eventParameters = "\"Secition_ID\": \"$sectionId\""
                                    accountManager.reportEvent(
                                        "Зашел в раздел (push)",
                                        eventParameters
                                    )

                                    findNavController().navigate(
                                        HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(
                                            sectionId.toLong()
                                        )
                                    )
                                }
                            }

                            "Karta" -> {
                                val orderId = it.orderId
                                if (!orderId.isNullOrEmpty()) {
                                    val eventParameters = "\"ID_Zakaz\": \"$orderId\""
                                    accountManager.reportEvent(
                                        "Зашел в заказ, статус в пути (push)",
                                        eventParameters
                                    )

                                    findNavController().navigate(
                                        HomeFragmentDirections.actionToOrderDetailsFragment(
                                            orderId.toLong()
                                        )
                                    )
                                }
                            }

                            "vsenovinki" -> {
                                findNavController().navigate(
                                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.NewProducts
                                    )
                                )
                            }

                            "vseskidki" -> {
                                findNavController().navigate(
                                    HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                                        PaginatedProductsCatalogWithoutFiltersFragment.DataSource.HurryBuyUpProducts
                                    )
                                )
                            }

                            "BRAND" -> {
                                val brandId = it.id
                                if (!brandId.isNullOrEmpty()) {
                                    findNavController().navigate(
                                        HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                                            PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(
                                                brandId.toLong()
                                            )
                                        )
                                    )
                                } else {
                                    findNavController().navigate(HomeFragmentDirections.actionToAllBrandsFragment())
                                }
                            }

                            "BRANDY" -> {
                                findNavController().navigate(HomeFragmentDirections.actionToAllBrandsFragment())
                                siteStateManager.clearPushListener()
                            }

                            "about" -> {
                                val section = it.section ?: return@collect
                                if (section == "О магазине") {
                                    findNavController().navigate(
                                        HomeFragmentDirections.actionToWebViewFragment(
                                            ApiConfig.ABOUT_SHOP_URL,
                                            "О магазине"
                                        )
                                    )
                                }
                                if (section == "Связаться с нами") {
                                    findNavController().navigate(HomeFragmentDirections.actionToContactsFragment())
                                }
                            }

                            "dostavka" -> {
                                findNavController().navigate(
                                    HomeFragmentDirections.actionToWebViewFragment(
                                        ApiConfig.ABOUT_DELIVERY_URL,
                                        "О доставке"
                                    )
                                )
                            }

                            "service" -> {
                                findNavController().navigate(HomeFragmentDirections.actionToAboutServicesDialogFragment())
                            }

                            "remont_kulerov" -> {
                                findNavController().navigate(HomeFragmentDirections.actionToAboutServicesDialogFragment())
                            }

                            "feedback" -> {
                                findNavController().navigate(HomeFragmentDirections.actionToContactsFragment())
                            }

                            "TOVARY" -> {

                            }

                            "ACTIONS" -> {

                            }

                            "vseakcii" -> {
                                findNavController().navigate(
                                    HomeFragmentDirections.actionToAllPromotionsFragment(
                                        AllPromotionsFragment.DataSource.All
                                    )
                                )
                            }

                            "URL" -> {
                                val url = it.id ?: return@collect
                                findNavController().navigate(
                                    HomeFragmentDirections.actionToWebViewFragment(
                                        url,
                                        ""
                                    )
                                )
                            }

                            "trekervodi" -> {
                                val eventName = "trekervodi_push"
                                accountManager.reportEvent(eventName)
                                findNavController().navigate(HomeFragmentDirections.actionToWaterAppFragment())
                            }

                            "profil" -> {
                                flowViewModel.goToProfile()
                            }

                            "pokypkasertificat" -> {
                                debugLog { "pokypkasertificat push" }
                                findNavController().navigate(HomeFragmentDirections.actionToBuyCertificateFragment())
                            }

                            null -> {}
                        }
                        it?.action?.let { action ->
                            if (action.contains("SOBNEW")) {
                                val eventParameters = "\"SOBNEW_NAME\": \"${it.id}\""
                                accountManager.reportEvent(
                                    "Зашел в приложение (push)",
                                    eventParameters
                                )
                            }
                        }
                        debugLog { "clear push" }
                        siteStateManager.clearPushListener()
                    }
            }
        }
    }


    private fun setCookie() {
        val webkitCookieManager = CookieManager.getInstance()
        webkitCookieManager.acceptCookie()
        webkitCookieManager.setCookie(
            ApiConfig.VODOVOZ_URL,
            cookieManager.fetchCookieSessionId()
        )
    }
}