package com.vodovoz.app.feature.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.media.MediaManager
import com.vodovoz.app.common.permissions.PermissionsController
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.speechrecognizer.SpeechDialogFragment
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentMainHomeFlowBinding
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.catalog.CatalogFragmentDirections
import com.vodovoz.app.feature.home.adapter.HomeMainClickListener
import com.vodovoz.app.feature.home.banneradvinfo.BannerAdvInfoBottomSheetFragment
import com.vodovoz.app.feature.home.popup.NewsClickListener
import com.vodovoz.app.feature.home.popup.PopupNewsBottomFragment
import com.vodovoz.app.feature.home.ratebottom.RateBottomViewModel
import com.vodovoz.app.feature.home.ratebottom.adapter.RateBottomClickListener
import com.vodovoz.app.feature.home.ratebottom.adapter.RateBottomImageAdapter
import com.vodovoz.app.feature.home.ratebottom.adapter.RateBottomViewPagerAdapter
import com.vodovoz.app.feature.home.viewholders.homebanners.BottomBannerManager
import com.vodovoz.app.feature.home.viewholders.homebanners.TopBannerManager
import com.vodovoz.app.feature.home.viewholders.homebanners.model.BannerAdvEntity
import com.vodovoz.app.feature.home.viewholders.homecomments.HomeCommentsFullBottomSheetFragment
import com.vodovoz.app.feature.home.viewholders.homeproducts.ProductsShowAllListener
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeTabsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.PromotionsClickListener
import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity
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
import com.vodovoz.app.feature.productdetail.sendcomment.SendCommentAboutProductBottomDialog
import com.vodovoz.app.feature.productlist.adapter.ProductsClickListener
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.util.extensions.addOnBackPressedCallback
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    lateinit var ratingProductManager: RatingProductManager

    private val collapsedImagesAdapter = RateBottomImageAdapter()
    private val rateBottomViewPagerAdapter =
        RateBottomViewPagerAdapter(object : RateBottomClickListener {

            override fun dontCommentProduct(id: Long) {
                ratingProductManager.dontCommentProduct(id)
                binding.rateBottom.visibility = View.GONE
//            dialog?.dismiss()
            }

            override fun rateProduct(id: Long, ratingCount: Int) {
                SendCommentAboutProductBottomDialog.newInstance(id, ratingCount)
                    .show(childFragmentManager, "TAG")
            }

        })

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
    lateinit var topBannerManager: TopBannerManager

    @Inject
    lateinit var bottomBannerManager: BottomBannerManager

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
            homeTabsClickListener = getHomeTabsClickListener(),
            topBannerManager = topBannerManager,
            bottomBannerManager = bottomBannerManager,
            showRateBottomSheetFragment = {
                if (siteStateManager.showRateBottom != null) {
                    if (!siteStateManager.showRateBottom!!) {
                        siteStateManager.showRateBottom = true
                        binding.rateBottom.visibility = View.VISIBLE
//                        val rateBottomSheet =  RateBottomFragment()
//                        rateBottomSheet.show(childFragmentManager, "TAG")
                    }
                }
            }
        ) {
            flowViewModel.repeatOrder(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rateBottomViewModel.firstLoad()
        observeUiState()
        observeTabReselect()
        observeEvents()
        observeDeepLinkFromSiteState()
        observeMediaManager()
        observePushFromSiteState()
        observeRateBottom()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeController.bind(binding.homeRv, binding.refreshContainer)
        initViewPager()
        initImageRv()
        initBottomSheetCallback()

        initSearchToolbar(
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment()) },
            { navigateToQrCodeFragment() },
            { startSpeechRecognizer() }
        )
        bindErrorRefresh { flowViewModel.refresh() }
        bindBackPressed()
    }

    private fun initViewPager() {
        binding.rateViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.rateViewPager.adapter = rateBottomViewPagerAdapter
        binding.dotsIndicator.attachTo(binding.rateViewPager)
    }

    private fun initImageRv() {
        with(binding.collapsedRv) {
            adapter = collapsedImagesAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    private fun initBottomSheetCallback() {
        val behavior = BottomSheetBehavior.from(binding.rateBottom)
        val density = requireContext().resources.displayMetrics.density
        behavior.peekHeight = (100 * density).toInt()
//        behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0) {
                    binding.collapsedLL.alpha = 1 - 2 * slideOffset
                    binding.expandedLL.alpha = slideOffset * slideOffset

                    if (slideOffset > 0.5) {
                        binding.collapsedLL.visibility = View.GONE
                        binding.expandedLL.visibility = View.VISIBLE
                        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    }

                    if (slideOffset < 0.5 && binding.expandedLL.visibility == View.VISIBLE) {
                        binding.collapsedLL.visibility = View.VISIBLE
                        binding.expandedLL.visibility = View.INVISIBLE
                    }
                } else {
                    binding.rateBottom.visibility = View.GONE
                }
            }
        })
    }

    private fun observeRateBottom() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                rateBottomViewModel
                    .observeUiState()
                    .collect { state ->

                        if (state.data.item != null) {
                            binding.expandedHeaderTv.text =
                                state.data.item.rateBottomData?.titleProduct
                            val prList = state.data.item.rateBottomData?.productsList
                            if (!prList.isNullOrEmpty()) {
                                rateBottomViewPagerAdapter.submitList(prList)
                                binding.dotsIndicator.isVisible = prList.size > 1
                            }
                        }

                        if (state.data.collapsedData != null) {
                            binding.collapsedBodyTv.text = state.data.collapsedData.body
                            binding.collapsedHeaderTv.text = state.data.collapsedData.title
                            if (!state.data.collapsedData.imageList.isNullOrEmpty()) {
                                collapsedImagesAdapter.submitList(state.data.collapsedData.imageList)
                            }
                        }

                        showError(state.error)

                        delay(2000)
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
                                    val eventParameters = "\"ID_Product\": \"$productId\""
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
                                    val eventParameters = "\"Secition_ID\": \"$sectionId\""
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
                                    val eventParameters = "\"ID_Zakaz\": \"$orderId\""
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

                            "trekervodi" -> {
                                val eventName = "trekervodi_push"
                                accountManager.reportYandexMetrica(eventName)
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
                                accountManager.reportYandexMetrica(
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

    private fun observeDeepLinkFromSiteState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                siteStateManager
                    .observeDeepLinkPath()
                    .collect {
                        when (it) {
                            /*"catalog" -> {
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
                        }*/
                            "mobile_app/" -> {
                                findNavController().navigate(HomeFragmentDirections.actionToAboutAppDialogFragment())
                            }

                            "gl/" -> {
                            }

                            "kalkulyator_vody/" -> {
                                val eventName = "trekervodi_ssilka"
                                accountManager.reportYandexMetrica(eventName)
                                findNavController().navigate(HomeFragmentDirections.actionToWaterAppFragment())
                            }
                        }

                        siteStateManager.clearDeepLinkListener()
                    }
            }
        }
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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

                            is HomeFlowViewModel.HomeEvents.GoToCart -> {
                                MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Товары добавлены в корзину")
                                    .setMessage("Перейти в корзину?")
                                    .setPositiveButton("Да") { dialog, _ ->
                                        dialog.dismiss()
                                        tabManager.selectTab(R.id.graph_cart)
                                    }
                                    .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                                    .show()
                            }
                        }
                    }
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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

            override fun onPromotionAdvClick(promotionAdvEntity: PromotionAdvEntity?) {
                BannerAdvInfoBottomSheetFragment
                    .newInstance(
                        promotionAdvEntity?.titleAdv ?: "",
                        promotionAdvEntity?.bodyAdv ?: "",
                        promotionAdvEntity?.dataAdv ?: ""
                    )
                    .show(childFragmentManager, "TAG")
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
                actionEntity?.let {
                    if (it is ActionEntity.WaterApp) {
                        val eventParameters = "\"source\":\"slayder\""
                        accountManager.reportYandexMetrica("trekervodi_zapysk", eventParameters)
                    }
                }
                actionEntity?.invoke(findNavController(), requireActivity())
            }

            override fun onBannerAdvClick(entity: BannerAdvEntity?) {
                BannerAdvInfoBottomSheetFragment
                    .newInstance(
                        entity?.titleAdv ?: "",
                        entity?.bodyAdv ?: "",
                        entity?.dataAdv ?: ""
                    )
                    .show(childFragmentManager, "TAG")
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
            override fun onCommentsClick(item: CommentUI) {
                HomeCommentsFullBottomSheetFragment.newInstance(
                    title = item.author ?: "Анонимно",
                    content = item.text ?: "",
                    rating = item.rating ?: 5,
                    date = item.date ?: ""
                ).show(childFragmentManager, "comm")
            }

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
                if (actionEntity is ActionEntity.WaterApp) {
                    val eventParameters = "\"source\":\"bottom_alert\""
                    accountManager.reportYandexMetrica("trekervodi_zapysk", eventParameters)
                }
                actionEntity.invoke()
            }
        }
    }

    private fun observeTabReselect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    }

    private fun bindBackPressed() {
        var back = false
        addOnBackPressedCallback {
            if (!back) {
                requireActivity().snack("Нажмите назад еще раз, чтобы выйти")
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
}