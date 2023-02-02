package com.vodovoz.app.ui.fragment.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.data.config.ApiConfig
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentMainHomeBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.fragment.all_promotions.AllPromotionsFragment
import com.vodovoz.app.ui.fragment.catalog.CatalogFragmentDirections
import com.vodovoz.app.ui.fragment.home.adapter.HomeMainClickListener
import com.vodovoz.app.ui.fragment.home_bottom_info.BottomInfoFragment
import com.vodovoz.app.ui.fragment.home_triple_navigation.TripleNavigationHomeFragment
import com.vodovoz.app.ui.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.fragment.popup_news.PopupNewsBottomFragment
import com.vodovoz.app.ui.fragment.products_catalog.ProductsCatalogFragment
import com.vodovoz.app.ui.fragment.slider.banners_slider.BannersSliderFragment
import com.vodovoz.app.ui.fragment.slider.brands_slider.BrandsSliderFragment
import com.vodovoz.app.ui.fragment.slider.comments_slider.CommentsSliderFragment
import com.vodovoz.app.ui.fragment.slider.countries_slider.CountriesSliderFragment
import com.vodovoz.app.ui.fragment.slider.histories_slider.HistorySliderFragment
import com.vodovoz.app.ui.fragment.slider.order_slider.OrdersSliderConfig
import com.vodovoz.app.ui.fragment.slider.order_slider.OrdersSliderFragment
import com.vodovoz.app.ui.fragment.slider.popular_slider.PopularCategoriesSliderFragment
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderFragment
import com.vodovoz.app.ui.fragment.slider.promotion_slider.PromotionsSliderFragment
import com.vodovoz.app.ui.interfaces.*
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : ViewStateBaseFragment(),
    IOnInvokeAction,
    IOnProductClick,
    IOnChangeProductQuantity,
    IOnBrandClick,
    IOnCountryClick,
    IOnCommentClick,
    IOnHistoryClick,
    IOnPopularCategoryClick,
    IOnPromotionClick,
    IOnOrderClick,
    IOnFavoriteClick
{

    companion object {
        const val IS_SHOW_POPUP_NEWS = "isShowPopupNews"
    }

    private lateinit var binding: FragmentMainHomeBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()

    private val advertisingBannersSliderFragment: BannersSliderFragment by lazy {
        BannersSliderFragment.newInstance(bannerRatio = 0.41) }
    private val historiesSliderFragment: HistorySliderFragment by lazy { HistorySliderFragment() }
    private val popularCategoriesSliderFragment: PopularCategoriesSliderFragment by lazy { PopularCategoriesSliderFragment() }
    private val categoryBannersSliderFragment: BannersSliderFragment by lazy {
        BannersSliderFragment.newInstance(bannerRatio = 0.5) }
    private val discountProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = true
        )) }
    private val topProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = true
        )) }
    private val ordersSliderFragment: OrdersSliderFragment by lazy {
        OrdersSliderFragment.newInstance(OrdersSliderConfig(
            containTitleContainer = true
        ))
    }
    private val tripleNavigationHomeFragment: TripleNavigationHomeFragment by lazy { TripleNavigationHomeFragment() }
    private val noveltiesProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = true
        )) }
    private val promotionsSliderFragment: PromotionsSliderFragment by lazy { PromotionsSliderFragment() }
    private val bottomProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = true
        )) }
    private val brandsSliderFragment: BrandsSliderFragment by lazy { BrandsSliderFragment() }
    private val countriesSliderFragment: CountriesSliderFragment by lazy { CountriesSliderFragment() }
    private val viewedProductsSliderFragment: ProductsSliderFragment by lazy {
        ProductsSliderFragment.newInstance(ProductsSliderConfig(
            containShowAllButton = false
        )) }
    private val commentsSliderFragment: CommentsSliderFragment by lazy { CommentsSliderFragment() }
    private val bottomInfoFragment: BottomInfoFragment by lazy { BottomInfoFragment() }

    private val homeController by lazy { HomeController(requireContext(), viewLifecycleOwner, flowViewModel, getMainClickListener()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initCallbacks()
        //observeUiState()
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
                findNavController().navigate(HomeFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_DELIVERY_URL,
                    "О доставке"
                ))
            }

            override fun onAboutPayClick() {
                findNavController().navigate(HomeFragmentDirections.actionToWebViewFragment(
                    ApiConfig.ABOUT_PAY_URL,
                    "Об оплате"
                ))
            }

            override fun onAboutShopClick() {
                findNavController().navigate(HomeFragmentDirections.actionToWebViewFragment(
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
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
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
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
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
                findNavController().navigate(R.id.favoriteFragment)
            }

            //POSITION_3
            override fun onCategoryClick(id: Long?) {
                id?.let {
                    findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(id))
                }
            }

            //POSITION_10
            override fun onPromotionProductClick(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToProductDetailFragment(id))
            }

            override fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String) {
                showPreOrderProductPopup(id, name, detailPicture)
            }

            override fun onChangeProductQuantity(id: Long, cartQuantity: Int) {
                flowViewModel.changeCart(id, cartQuantity)
            }

            override fun onFavoriteClick(id: Long, isFavorite: Boolean) {
                flowViewModel.changeFavoriteStatus(id, isFavorite)
            }

            override fun onPromotionClick(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToPromotionDetailFragment(id))
            }

            override fun onShowAllPromotionsClick() {
                findNavController().navigate(HomeFragmentDirections.actionToAllPromotionsFragment(
                    AllPromotionsFragment.DataSource.All()
                ))
            }

            //POSITION_4, POSITION_6, POSITION_9, POSITION_11, POSITION_14
            override fun showAllDiscountProducts(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
                ))
            }

            override fun showAllTopProducts(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(id)
                ))
            }

            override fun showAllNoveltiesProducts(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
                ))
            }

            override fun showAllBottomProducts(id: Long) {
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(id)
                ))
            }
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            flowViewModel.observeUiState()
                .collect { homeState ->

                    if (homeState.loadingPage) { onStateLoading() }

                    if (homeState.data.items.size == HomeFlowViewModel.POSITIONS_COUNT) {
                        onStateSuccess()
                        val list =
                            homeState.data.items.sortedBy { it.position }.mapNotNull { it.item }
                        homeController.submitList(list)
                    }

                    debugLog { "${homeState.data.items.map { it.position }.sorted()}" }
                }
        }
    }

    private fun getArgs() {
        viewModel.updateArgs(HomeFragmentArgs.fromBundle(requireArguments()).isShowPopupNews)
        viewModel.firstLoad()
    }

    private fun initCallbacks() {
        advertisingBannersSliderFragment.initCallbacks(iOnInvokeAction = this)
        historiesSliderFragment.initCallbacks(iOnHistoryClick = this)
        popularCategoriesSliderFragment.initCallbacks(iOnPopularCategoryClick = this)
        discountProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnFavoriteClick = this,
            iOnShowAllProductsClick = {
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Discount()
                ))
            },
            onNotifyWhenBeAvailable = { id, name, picture -> showPreOrderProductPopup(id, name, picture) },
            onNotAvailableMore = {}
        )
        categoryBannersSliderFragment.initCallbacks( iOnInvokeAction = this)
        topProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnFavoriteClick = this,
            iOnShowAllProductsClick = { categoryId ->
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(categoryId)
                ))
            },
            onNotifyWhenBeAvailable = { id, name, picture -> showPreOrderProductPopup(id, name, picture) },
            onNotAvailableMore = {}
        )
        ordersSliderFragment.initCallbacks(
            iOnOrderClick = { orderId ->
                findNavController().navigate(HomeFragmentDirections.actionToOrderDetailsFragment(orderId))
            },
            iOnShowAllOrdersClick = {
                findNavController().navigate(HomeFragmentDirections.actionToAllOrdersFragment())
            }
        )
        tripleNavigationHomeFragment.initCallbacks(
            iOnLastPurchasesClick = {
                findNavController().navigate(HomeFragmentDirections.actionToPastPurchasesFragment())
            },
            iOnOrdersHistoryClick = {
                findNavController().navigate(HomeFragmentDirections.actionToAllOrdersFragment())
            },
            iOnShowAllFavoriteClick = {
                findNavController().navigate(R.id.favoriteFragment)
            }
        )
        noveltiesProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnFavoriteClick = this,
            iOnShowAllProductsClick = {
                Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Novelties()
                ))
            },
            onNotifyWhenBeAvailable = { id, name, picture -> showPreOrderProductPopup(id, name, picture) },
            onNotAvailableMore = {}
        )
        promotionsSliderFragment.initCallbacks(
            iOnPromotionClick = this,
            iOnProductClick = this,
            iOnShowAllPromotionsClick = {
                findNavController().navigate(HomeFragmentDirections.actionToAllPromotionsFragment(
                    AllPromotionsFragment.DataSource.All()
                ))
            },
            iOnChangeProductQuantity = this,
            iOnFavoriteClick = this,
            onNotAvailableMore = {},
            onNotifyWhenBeAvailable = { id, name, picture -> showPreOrderProductPopup(id, name, picture) }
        )
        bottomProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnFavoriteClick = this,
            iOnShowAllProductsClick = { categoryId ->
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(categoryId)
                ))
            },
            onNotifyWhenBeAvailable = { id, name, picture -> showPreOrderProductPopup(id, name, picture) },
            onNotAvailableMore = {}
        )
        brandsSliderFragment.initCallbacks(
            iOnBrandClick = this,
            iOnShowAllBrandsClick = {
                findNavController().navigate(HomeFragmentDirections.actionToAllBrandsFragment())
            }
        )
        countriesSliderFragment.initCallbacks(iOnCountryClick = this)
        viewedProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnFavoriteClick = this,
            iOnShowAllProductsClick = {},
            onNotifyWhenBeAvailable = { id, name, picture -> showPreOrderProductPopup(id, name, picture) },
            onNotAvailableMore = {}
        )
        commentsSliderFragment.initCallbacks(
            iOnCommentClick = this,
            iOnSendCommentAboutShop = {
                if (viewModel.isLoginAlready()) {
                    findNavController().navigate(HomeFragmentDirections.actionToSendCommentAboutShopBottomDialog())
                } else {
                    findNavController().navigate(R.id.profileFragment)
                }
            }
        )
    }

    override fun update() { viewModel.refresh() }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainHomeBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        loadFragments()
        initOther()
        observeViewModel()
    }

    private fun loadFragments() {
        replaceFragment(R.id.advertisingBannersSliderFragment, advertisingBannersSliderFragment)
        replaceFragment(R.id.historiesSliderFragment, historiesSliderFragment)
        replaceFragment(R.id.popularCategoriesSliderFragment, popularCategoriesSliderFragment)
        replaceFragment(R.id.discountProductsSliderFragment, discountProductsSliderFragment)
        replaceFragment(R.id.categoryBannersSliderFragment, categoryBannersSliderFragment)
        replaceFragment(R.id.topProductsSliderFragment, topProductsSliderFragment)
        replaceFragment(R.id.ordersSliderFragment, ordersSliderFragment)
        replaceFragment(R.id.tripleNavigationHomeFragment, tripleNavigationHomeFragment)
        replaceFragment(R.id.noveltiesProductsSliderFragment, noveltiesProductsSliderFragment)
        replaceFragment(R.id.promotionsSliderFragment, promotionsSliderFragment)
        replaceFragment(R.id.bottomProductsSliderFragment, bottomProductsSliderFragment)
        replaceFragment(R.id.brandsSliderFragment, brandsSliderFragment)
        replaceFragment(R.id.countriesSliderFragment, countriesSliderFragment)
        replaceFragment(R.id.viewedProductsSliderFragment, viewedProductsSliderFragment)
        replaceFragment(R.id.commentsSliderFragment, commentsSliderFragment)
        replaceFragment(R.id.commentsSliderFragment, commentsSliderFragment)
        replaceFragment(R.id.bottomInfoFragment, bottomInfoFragment)
    }

    private fun replaceFragment(id: Int, fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(id, fragment).commit()
    }

    private fun initOther() {
        binding.refreshContainer.setOnRefreshListener {
            update()
        }
        binding.searchContainer.clSearchContainer.setOnClickListener {
            findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment())
        }
        binding.searchContainer.etSearch.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment())
            }
        }

       // binding.contentContainer.setScrollElevation(binding.appBar)
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> onStateHide()
                is ViewState.Loading -> onStateLoading()
                is ViewState.Error -> onStateError(state.errorMessage)
                is ViewState.Success -> {
                    onStateSuccess()
                    binding.refreshContainer.isRefreshing = false
                }
            }
        }

        viewModel.popupNewsUILD.observe(viewLifecycleOwner) { popupNewsUI ->
            if (viewModel.isShowPopupNews) {
                viewModel.isShowPopupNews = false
                val dialog = PopupNewsBottomFragment.newInstance(
                    popupNewsUI,
                    iOnInvokeAction = { action -> action.invoke()}
                )

                dialog.show(childFragmentManager, dialog::class.simpleName)
            }
        }

        viewModel.errorLD.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.advertisingBannersSliderDataLD.observe(viewLifecycleOwner) { bannerUIList ->
            advertisingBannersSliderFragment.updateData(bannerUIList)
        }

        viewModel.advertisingBannersSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> advertisingBannersSliderFragment.hide()
                false -> advertisingBannersSliderFragment.show()
            }
        }

        viewModel.historiesSliderDataLD.observe(viewLifecycleOwner) { historyUIList ->
            historiesSliderFragment.updateData(historyUIList)
        }

        viewModel.historiesSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> historiesSliderFragment.hide()
                false -> historiesSliderFragment.show()
            }
        }

        viewModel.popularCategoriesSliderDataLD.observe(viewLifecycleOwner) { categoryUIList ->
            popularCategoriesSliderFragment.updateData(categoryUIList)
        }

        viewModel.popularCategoriesSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> popularCategoriesSliderFragment.hide()
                false -> popularCategoriesSliderFragment.show()
            }
        }

        viewModel.discountProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            discountProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.discountProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> discountProductsSliderFragment.hide()
                false -> discountProductsSliderFragment.show()
            }
        }

        viewModel.categoryBannersSliderDataLD.observe(viewLifecycleOwner) { bannerUIList ->
            categoryBannersSliderFragment.updateData(bannerUIList)
        }

        viewModel.categoryBannersSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> categoryBannersSliderFragment.hide()
                false -> categoryBannersSliderFragment.show()
            }
        }

        viewModel.topProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            topProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.topProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> topProductsSliderFragment.hide()
                false -> topProductsSliderFragment.show()
            }
        }

        viewModel.ordersSliderDataLD.observe(viewLifecycleOwner) { orderUIList ->
            ordersSliderFragment.updateData(orderUIList)
        }

        viewModel.ordersSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> ordersSliderFragment.hide()
                false -> ordersSliderFragment.show()
            }
        }

        viewModel.noveltiesProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            noveltiesProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.noveltiesProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> noveltiesProductsSliderFragment.hide()
                false -> noveltiesProductsSliderFragment.show()
            }
        }

        viewModel.promotionsSliderDataLD.observe(viewLifecycleOwner) { promotionsSliderBundleUI ->
            promotionsSliderFragment.updateData(promotionsSliderBundleUI)
        }

        viewModel.promotionsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> promotionsSliderFragment.hide()
                false -> promotionsSliderFragment.show()
            }
        }

        viewModel.bottomProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            bottomProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.bottomProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> bottomProductsSliderFragment.hide()
                false -> bottomProductsSliderFragment.show()
            }
        }

        viewModel.brandsSliderDataLD.observe(viewLifecycleOwner) { brandUIList ->
            brandsSliderFragment.updateData(brandUIList)
        }

        viewModel.brandsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> brandsSliderFragment.hide()
                false -> brandsSliderFragment.show()
            }
        }

        viewModel.countriesSliderDataLD.observe(viewLifecycleOwner) { countriesSliderBundleUI ->
            countriesSliderFragment.updateData(countriesSliderBundleUI)
        }

        viewModel.countriesSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> countriesSliderFragment.hide()
                false -> countriesSliderFragment.show()
            }
        }

        viewModel.viewedProductsSliderDataLD.observe(viewLifecycleOwner) { categoryDetailUIList ->
            viewedProductsSliderFragment.updateData(categoryDetailUIList)
        }

        viewModel.viewedProductsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> viewedProductsSliderFragment.hide()
                false -> viewedProductsSliderFragment.show()
            }
        }

        viewModel.commentsSliderDataLD.observe(viewLifecycleOwner) { commentUIList ->
            commentsSliderFragment.updateData(commentUIList)
        }

        viewModel.commentsSliderHideLD.observe(viewLifecycleOwner) { isHide ->
            when(isHide) {
                true -> commentsSliderFragment.hide()
                false -> commentsSliderFragment.show()
            }
        }
    }

    override fun onInvokeAction(actionEntity: ActionEntity) {
        actionEntity.invoke(findNavController(), requireActivity())
    }

    override fun onProductClick(productId: Long) {
        findNavController().navigate(HomeFragmentDirections.actionToProductDetailFragment(productId))
    }

    override fun onChangeProductQuantity(pair: Pair<Long, Int>) {
        viewModel.changeCart(pair.first, pair.second)
    }

    override fun onBrandClick(brandId: Long) {
        findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
            PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId)
        ))
    }

    override fun onCountryClick(countryId: Long) {
        findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
            PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Country(countryId)
        ))
    }

    override fun onCommentClick(commentId: Long) {

    }

    override fun onHistoryClick(historyId: Long) {
        findNavController().navigate(HomeFragmentDirections.actionToFullScreenHistorySliderFragment(historyId))
    }

    override fun onPopularCategoryClick(categoryId: Long) {
        findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(categoryId))
    }

    override fun onPromotionClick(promotionId: Long) {
        findNavController().navigate(HomeFragmentDirections.actionToPromotionDetailFragment(promotionId))
    }

    override fun onOrderClick(orderId: Long) {

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

    override fun onFavoriteClick(pair: Pair<Long, Boolean>) {
        viewModel.changeFavoriteStatus(pair.first, pair.second)
    }

    private fun showPreOrderProductPopup(id: Long, name: String, picture: String) {
        when(viewModel.isLoginAlready()) {
            true -> findNavController().navigate(HomeFragmentDirections.actionToPreOrderBS(id, name, picture))
            false -> findNavController().navigate(HomeFragmentDirections.actionToProfileFragment())
        }
    }

}