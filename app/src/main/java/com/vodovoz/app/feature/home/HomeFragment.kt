package com.vodovoz.app.feature.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.media.MediaManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
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
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeTabsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.BRANDS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.COMMENTS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.DISCOUNT_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.NOVELTIES_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.ORDERS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.PROMOTIONS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.VIEWED_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitleClickListener
import com.vodovoz.app.feature.onlyproducts.ProductsCatalogFragment
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.util.extensions.addOnBackPressedCallback
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
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

    internal val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val rateBottomViewModel: RateBottomViewModel by activityViewModels()

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

    private val homeController by lazy {
        HomeController(
            viewModel = flowViewModel,
            cartManager = cartManager,
            likeManager = likeManager,
            listener = getMainClickListener(),
            productsShowAllListener = getProductsShowClickListener(),
            productsClickListener = getProductsClickListener(),
            promotionsClickListener = getPromotionsClickListener(),
            homeTitleClickListener = getTitleClickListener(),
            homeTabsClickListener = getHomeTabsClickListener()
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
        rateBottomViewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeController.bind(binding.homeRv, binding.refreshContainer)
        initSearchToolbar(
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { navigateToQrCodeFragment() },
            { startSpeechRecognizer() }
        )
        bindErrorRefresh { flowViewModel.refresh() }
        observeUiState()
        observeTabReselect()
        observeEvents()
        bindBackPressed()
        observeDeepLinkFromSiteState()
        observeMediaManager()
        observePushFromSiteState()
    }

    private fun observePushFromSiteState() {
        lifecycleScope.launchWhenResumed {
            siteStateManager
                .observePush()
                .collect {
                    when (it?.path) {
                        "AKCII" -> {
                            val promotionId = it.id
                            if (!promotionId.isNullOrEmpty()) {
                                val eventParameters = "ID_AKCII: $promotionId"
                                accountManager.reportYandexMetrica(
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
                                val eventParameters = "ID_Product: $productId"
                                accountManager.reportYandexMetrica(
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
                                val eventParameters = "Secition_ID: $sectionId"
                                accountManager.reportYandexMetrica(
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
                                val eventParameters = "ID_Zakaz: $orderId"
                                accountManager.reportYandexMetrica(
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
                                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
                                )
                            )
                        }
                        "vseskidki" -> {
                            findNavController().navigate(
                                HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
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
                                    AllPromotionsFragment.DataSource.All()
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
                        null -> {}
                    }
                }
        }
    }

    private fun observeDeepLinkFromSiteState() {
        lifecycleScope.launchWhenStarted {
            siteStateManager
                .observeDeepLinkPath()
                .collect {
                    when (it) {
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
                        "basket" -> {
                            tabManager.selectTab(R.id.graph_cart)
                        }
                        "gl" -> {
                            siteStateManager.clearDeepLinkListener()
                        }
                        "kalkulyator_vody" -> {
                            findNavController().navigate(HomeFragmentDirections.actionToWaterAppFragment())
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
                    when (it) {
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
                            if (findNavController().currentBackStackEntry?.destination?.id == R.id.sendCommentAboutShopBottomDialog) {
                                findNavController().popBackStack()
                            }
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

                    if (homeState.loadingPage) {
                        showLoaderWithBg(true)
                    } else {
                        showLoaderWithBg(false)
                    }

                    if (homeState.data.news?.androidVersion.isNullOrEmpty()) {
                        if (homeState.data.news != null && !homeState.data.hasShow) {
                            showPopUpNews(homeState.data.news)
                        }
                    } else {
                        if (homeState.data.news?.androidVersion != null) {
                            if (homeState.data.news.androidVersion > BuildConfig.VERSION_NAME) {
                                showPopUpNews(homeState.data.news)
                            }
                        }
                    }

                    val list = homeState.data.items
                    val progressList = if (!homeState.data.isSecondLoad) {
                        list + BottomProgressItem()
                    } else {
                        list
                    }
                    homeController.submitList(progressList)

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

    private fun getTitleClickListener(): HomeTitleClickListener {
        return object : HomeTitleClickListener {
            override fun onShowAllTitleClick(item: HomeTitle) {
                when (item.type) {
                    DISCOUNT_TITLE -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
                            )
                        )
                    }
                    ORDERS_TITLE -> {
                        findNavController().navigate(HomeFragmentDirections.actionToAllOrdersFragment())
                    }
                    NOVELTIES_TITLE -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
                            )
                        )
                    }
                    PROMOTIONS_TITLE -> {
                        findNavController().navigate(
                            HomeFragmentDirections.actionToAllPromotionsFragment(
                                AllPromotionsFragment.DataSource.All()
                            )
                        )
                    }
                    BRANDS_TITLE -> {
                        findNavController().navigate(HomeFragmentDirections.actionToAllBrandsFragment())
                    }
                    VIEWED_TITLE -> {

                    }
                    COMMENTS_TITLE -> {
                        flowViewModel.onSendCommentClick()
                    }
                }
            }

        }
    }

    private fun getHomeTabsClickListener(): HomeTabsClickListener {
        return object : HomeTabsClickListener {
            override fun onCategoryClick(categoryId: Long?, position: Int?) {
                if (categoryId == null || position == null) return

                flowViewModel.updateProductsSliderByCategory(position, categoryId)
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

    internal fun ActionEntity.invoke(
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

    private fun newsClickListener(): NewsClickListener {
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

    private val permissionsController by lazy {
        PermissionsController(requireContext())
    }

    private fun navigateToQrCodeFragment() {
        permissionsController.methodRequiresCameraPermission(requireActivity()) {
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

    private fun startSpeechRecognizer() {
        permissionsController.methodRequiresRecordAudioPermission(requireActivity()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@methodRequiresRecordAudioPermission
            }

            SpeechDialogFragment().show(childFragmentManager, "TAG")

        }
    }

    private fun observeMediaManager() {
        lifecycleScope.launchWhenStarted {
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