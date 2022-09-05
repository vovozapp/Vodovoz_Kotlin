package com.vodovoz.app.ui.fragment.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.databinding.FragmentMainHomeBinding
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.base.ViewStateBaseFragment
import com.vodovoz.app.ui.base.VodovozApplication
import com.vodovoz.app.ui.extensions.ScrollViewExtensions.setScrollElevation
import com.vodovoz.app.ui.fragment.all_promotions.AllPromotionsFragment
import com.vodovoz.app.ui.fragment.catalog.CatalogFragmentDirections
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
import com.vodovoz.app.util.LogSettings

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
    private lateinit var viewModel: HomeViewModel

    private val advertisingBannersSliderFragment: BannersSliderFragment by lazy {
        BannersSliderFragment.newInstance(bannerRatio = 0.41) }
    private val historiesSliderFragment: HistorySliderFragment by lazy { HistorySliderFragment() }
    private val popularCategoriesSliderFragment: PopularCategoriesSliderFragment by lazy { PopularCategoriesSliderFragment() }
    private val categoryBannersSliderFragment: BannersSliderFragment by lazy {
        BannersSliderFragment.newInstance(bannerRatio = 0.48) }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        getArgs()
        initCallbacks()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[HomeViewModel::class.java]
    }

    private fun getArgs() {
        viewModel.updateArgs(HomeFragmentArgs.fromBundle(requireArguments()).isShowPopupNews)
        viewModel.updateData()
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
            }
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
            }
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
            }
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
            iOnFavoriteClick = this
        )
        bottomProductsSliderFragment.initCallbacks(
            iOnProductClick = this,
            iOnChangeProductQuantity = this,
            iOnFavoriteClick = this,
            iOnShowAllProductsClick = { categoryId ->
                findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                    PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Slider(categoryId)
                ))
            }
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
            iOnShowAllProductsClick = {}
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

    override fun update() { viewModel.updateData() }

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
        childFragmentManager.beginTransaction().replace(
            R.id.advertisingBannersSliderFragment,
            advertisingBannersSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.historiesSliderFragment,
            historiesSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.popularCategoriesSliderFragment,
            popularCategoriesSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.discountProductsSliderFragment,
            discountProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.categoryBannersSliderFragment,
            categoryBannersSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.topProductsSliderFragment,
            topProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.ordersSliderFragment,
            ordersSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.tripleNavigationHomeFragment,
            tripleNavigationHomeFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.noveltiesProductsSliderFragment,
            noveltiesProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.promotionsSliderFragment,
            promotionsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.bottomProductsSliderFragment,
            bottomProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.brandsSliderFragment,
            brandsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.countriesSliderFragment,
            countriesSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.viewedProductsSliderFragment,
            viewedProductsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.commentsSliderFragment,
            commentsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.commentsSliderFragment,
            commentsSliderFragment
        ).commit()

        childFragmentManager.beginTransaction().replace(
            R.id.bottomInfoFragment,
            bottomInfoFragment
        ).commit()
    }

    private fun initOther() {
        binding.refreshContainer.setOnRefreshListener {
            update()
        }
        binding.searchContainer.searchRoot.setOnClickListener {
            findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment())
        }
        binding.searchContainer.search.setOnFocusChangeListener { _, isFocusable ->
            if (isFocusable) {
                findNavController().navigate(CatalogFragmentDirections.actionToSearchFragment())
            }
        }

        binding.contentContainer.setScrollElevation(binding.appBar)
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
                HomeFragmentDirections.actionToAllBrandsFragment().also { navDirect ->
                    navDirect.brandIdList = this.brandIdList.toLongArray()
                }
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

}