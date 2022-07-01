package com.vodovoz.app.ui.components.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import com.vodovoz.app.data.model.common.BannerActionEntity
import com.vodovoz.app.databinding.FragmentMainHomeBinding
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseFragment
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.fragment.all_promotions.AllPromotionsFragment
import com.vodovoz.app.ui.components.fragment.slider.banner_slider.BannerSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.banner_slider.BannerSliderViewModel
import com.vodovoz.app.ui.components.fragment.slider.brand_slider.BrandSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.comment_slider.CommentSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.country_slider.CountrySliderFragment
import com.vodovoz.app.ui.components.fragment.slider.history_slider.HistorySliderFragment
import com.vodovoz.app.ui.components.fragment.home_bottom_info.AdditionalInfoSectionFragment
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersFragment
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog_without_filters.PaginatedProductsCatalogWithoutFiltersViewModel
import com.vodovoz.app.ui.components.fragment.slider.order_slider.OrderSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.popular_slider.PopularSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.product_slider.ProductSliderFragment
import com.vodovoz.app.ui.components.fragment.slider.product_slider.ProductSliderViewModel
import com.vodovoz.app.ui.components.fragment.slider.promotion_slider.PromotionSliderFragment
import com.vodovoz.app.ui.extensions.BannerActionExtensions.invoke
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class HomeFragment : ViewStateBaseFragment() {

    private lateinit var binding: FragmentMainHomeBinding
    private lateinit var viewModel: HomeViewModel

    private val compositeDisposable = CompositeDisposable()

    private val space: Int by lazy { resources.getDimension(R.dimen.primary_space).toInt() }
    private var isFirstUpdate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[HomeViewModel::class.java]
    }

    override fun setContentView(
        inflater: LayoutInflater,
        container: ViewGroup
    ) = FragmentMainHomeBinding.inflate(
        inflater,
        container,
        false
    ).apply { binding = this }.root

    override fun initView() {
        initSubjects()
        loadFragments()
        initScrollLayout()
    }

    override fun update() {
        onUpdateAdvertisingBannerSliderSubject.onNext(true)
        onUpdateHistorySliderSubject.onNext(true)
        onUpdatePopularCategorySliderSubject.onNext(true)
        onUpdateDiscountSliderSubject.onNext(true)
        onUpdateCategoryBannerSliderSubject.onNext(true)
        onUpdateTopSliderSubject.onNext(true)
        //onUpdateOrderSliderSubject.onNext(true)
        onUpdateNoveltiesSliderSubject.onNext(true)
        onUpdatePromotionsSliderSubject.onNext(true)
        onUpdateBottomSliderSubject.onNext(true)
        onUpdateBrandSliderSubject.onNext(true)
        onUpdateCountrySliderSubject.onNext(true)
        onUpdateViewedSliderSubject.onNext(true)
        onUpdateCommentSliderSubject.onNext(true)
    }

    private fun initScrollLayout() {
//        binding.contentContainer.viewTreeObserver.addOnScrollChangedListener {
//            binding.appBar.translationZ =
//                if (binding.contentContainer.canScrollVertically(-1)) 16f
//                else 0f
//        }

        binding.refreshContainer.setOnRefreshListener {
            update()
        }
    }

    //Общие subjects
    private val onProductClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onShowAllProductsSubject: PublishSubject<PaginatedProductsCatalogWithoutFiltersFragment.DataSource> = PublishSubject.create()

    init {
        onProductClickSubject.subscribeBy { productId ->
            findNavController().navigate(HomeFragmentDirections.actionToProductDetailFragment(productId))
        }.addTo(compositeDisposable)

        onShowAllProductsSubject.subscribeBy { dataSource ->
            findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(dataSource))
        }.addTo(compositeDisposable)
    }

    //Слайдер рекламных баннеров
    private val viewStateAdvertisingBannerSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateAdvertisingBannerSliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onAdvertisingBannerClickSubject: PublishSubject<BannerActionEntity> = PublishSubject.create()

    init {
        onAdvertisingBannerClickSubject.subscribeBy { bannerActionEntity ->
            bannerActionEntity.invoke(
                navController = findNavController(),
                activity = requireActivity()
            )
        }.addTo(compositeDisposable)
    }

    private fun loadAdvertisingBannerSlider() {
        childFragmentManager.beginTransaction().replace(R.id.mainBannersFragment, BannerSliderFragment.newInstance(
            viewStateSubject = viewStateAdvertisingBannerSliderSubject,
            onUpdateSubject = onUpdateAdvertisingBannerSliderSubject,
            onBannerClickSubject = onAdvertisingBannerClickSubject,
            bannerSliderConfig = BannerSliderFragment.BannerSliderConfig(
                bannerType = BannerSliderViewModel.ADVERTISING_BANNERS_SLIDER,
                marginTop = space/2,
                marginBottom = space/2,
                marginLeft = space,
                marginRight = space,
                ratio = 0.41
            )
        )).commit()
    }

    //Слайдер историй
    private val viewStateHistorySliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateHistorySliderSubject: PublishSubject<Boolean> = PublishSubject.create()

    private fun loadHistorySlider() {
        childFragmentManager.beginTransaction().replace(R.id.historiesFragment, HistorySliderFragment.newInstance(
            viewStateSubject = viewStateHistorySliderSubject,
            onUpdateSubject = onUpdateHistorySliderSubject
        )).commit()
    }

    //Слайдер популярных разделов
    private val viewStatePopularCategorySliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdatePopularCategorySliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onPopularCategoryClickSubject: PublishSubject<Long> = PublishSubject.create()

    init {
        onPopularCategoryClickSubject.subscribeBy { categoryId ->
            findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogFragment(categoryId))
        }.addTo(compositeDisposable)
    }

    private fun loadPopularCategorySlider() {
        childFragmentManager.beginTransaction().replace(R.id.popularFragment, PopularSliderFragment.newInstance(
            viewStateSubject = viewStatePopularCategorySliderSubject,
            onUpdateSubject = onUpdatePopularCategorySliderSubject,
            onPopularCategoryClickSubject = onPopularCategoryClickSubject
        )).commit()
    }

    //Слайдер самых выгодных продуктов
    private val viewStateDiscountSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateDiscountSliderSubject: PublishSubject<Boolean> = PublishSubject.create()

    private fun loadDiscountSlider() {
        childFragmentManager.beginTransaction().replace(R.id.discountFragment, ProductSliderFragment.newInstance(
            dataSource = ProductSliderFragment.DataSource.Request(
                sliderType = ProductSliderViewModel.DISCOUNT_PRODUCTS_SLIDER
            ),
            config = ProductSliderFragment.Config(false),
            viewStateSubject = viewStateDiscountSliderSubject,
            onUpdateSubject  = onUpdateDiscountSliderSubject,
            onProductClickSubject = onProductClickSubject,
            onShowAllClickSubject = onShowAllProductsSubject
        )).commit()
    }

    //Слайдер баннеров категорий
    private val viewStateCategoryBannerSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateCategoryBannerSliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onCategoryBannerClickSubject: PublishSubject<BannerActionEntity> = PublishSubject.create()

    init {
        onCategoryBannerClickSubject.subscribeBy { bannerActionEntity ->
            bannerActionEntity.invoke(
                navController = findNavController(),
                activity = requireActivity()
            )
        }.addTo(compositeDisposable)
    }

    private fun loadCategoryBannerSlider() {
        childFragmentManager.beginTransaction().replace(R.id.secondaryBannersFragment, BannerSliderFragment.newInstance(
            viewStateSubject = viewStateCategoryBannerSliderSubject,
            onUpdateSubject = onUpdateCategoryBannerSliderSubject,
            onBannerClickSubject = onCategoryBannerClickSubject,
            bannerSliderConfig = BannerSliderFragment.BannerSliderConfig(
                bannerType = BannerSliderViewModel.CATEGORY_BANNERS_SLIDER,
                marginTop = space/2,
                marginBottom = space/2,
                marginLeft = space,
                marginRight = space,
                ratio = 0.48
            )
        )).commit()
    }

    //Верхний слайдер продуктов
    private val viewStateTopSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateTopSliderSubject: PublishSubject<Boolean> = PublishSubject.create()

    private fun loadTopSlider() {
        childFragmentManager.beginTransaction().replace(R.id.topProductSlider, ProductSliderFragment.newInstance(
            dataSource = ProductSliderFragment.DataSource.Request(
                sliderType = ProductSliderViewModel.BOTTOM_PRODUCTS_SLIDER
            ),
            config = ProductSliderFragment.Config(false),
            viewStateSubject = viewStateTopSliderSubject,
            onUpdateSubject  = onUpdateTopSliderSubject,
            onProductClickSubject = onProductClickSubject,
            onShowAllClickSubject = onShowAllProductsSubject
        )).commit()
    }

    //Слайдер прошлых заказов
    private val viewStateOrderSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateOrderSliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onOrderClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onShowAllOrdersClickSubject: PublishSubject<Boolean> = PublishSubject.create()

    private fun loadOrderSlider() {
        childFragmentManager.beginTransaction().replace(R.id.orderSliderFragment, OrderSliderFragment.newInstance(
            viewStateSubject = viewStateOrderSliderSubject,
            onUpdateSubject  = onUpdateOrderSliderSubject,
            onOrderClickSubject = onOrderClickSubject,
            onShowAllOrdersClickSubject = onShowAllOrdersClickSubject
        )).commit()
    }

    //Тройная навигация

    //Слайдер новинок
    private val viewStateNoveltiesSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateNoveltiesSliderSubject: PublishSubject<Boolean> = PublishSubject.create()

    private fun loadNoveltiesSlider() {
        childFragmentManager.beginTransaction().replace(R.id.noveltiesFragment, ProductSliderFragment.newInstance(
            dataSource = ProductSliderFragment.DataSource.Request(
                sliderType = ProductSliderViewModel.NOVELTIES_PRODUCTS_SLIDER
            ),
            config = ProductSliderFragment.Config(false),
            viewStateSubject = viewStateNoveltiesSliderSubject,
            onUpdateSubject  = onUpdateNoveltiesSliderSubject,
            onProductClickSubject = onProductClickSubject,
            onShowAllClickSubject = onShowAllProductsSubject
        )).commit()
    }

    //Слайдер акций
    private val onReadyPromotionsSliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val viewStatePromotionsSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdatePromotionsSliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onPromotionClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onShowAllPromotionsClickSubject: PublishSubject<Boolean> = PublishSubject.create()

    init {
        onPromotionClickSubject.subscribeBy { promotionId ->
            findNavController().navigate(HomeFragmentDirections.actionToPromotionDetailFragment(promotionId))
        }.addTo(compositeDisposable)

        onShowAllPromotionsClickSubject.subscribeBy {
            findNavController().navigate(HomeFragmentDirections.actionToAllPromotionsFragment(
                AllPromotionsFragment.DataSource.All()
            ))
        }.addTo(compositeDisposable)
    }

    private fun loadPromotionSlider() {
        childFragmentManager.beginTransaction().replace(R.id.promotionSliderFragment, PromotionSliderFragment.newInstance(
            dataSource = PromotionSliderFragment.DataSource.Request("Акции"),
            onReadyViewSubject = onReadyPromotionsSliderSubject,
            viewStateSubject = viewStatePromotionsSliderSubject,
            onUpdateSubject  = onUpdatePromotionsSliderSubject,
            onProductClickSubject = onProductClickSubject,
            onShowAllPromotionSubject = onShowAllPromotionsClickSubject,
            onPromotionClickSubject = onPromotionClickSubject
        )).commit()
    }

    //Нижний слайдер продуктов
    private val viewStateBottomSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateBottomSliderSubject: PublishSubject<Boolean> = PublishSubject.create()

    private fun loadBottomSlider() {
        childFragmentManager.beginTransaction().replace(R.id.bottomProductSlider, ProductSliderFragment.newInstance(
            dataSource = ProductSliderFragment.DataSource.Request(
                sliderType = ProductSliderViewModel.TOP_PRODUCTS_SLIDER
            ),
            config = ProductSliderFragment.Config(false),
            viewStateSubject = viewStateBottomSliderSubject,
            onUpdateSubject  = onUpdateBottomSliderSubject,
            onProductClickSubject = onProductClickSubject,
            onShowAllClickSubject = onShowAllProductsSubject
        )).commit()
    }

    //Слайдер брендов
    private val viewStateBrandSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateBrandSliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onBrandClickSubject: PublishSubject<Long> = PublishSubject.create()
    private val onShowAllBrandClickSubject: PublishSubject<Boolean> = PublishSubject.create()

    init {
        onBrandClickSubject.subscribeBy { brandId ->
            findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Brand(brandId)
            ))
        }.addTo(compositeDisposable)

        onShowAllBrandClickSubject.subscribeBy {
            findNavController().navigate(HomeFragmentDirections.actionToAllBrandsFragment(longArrayOf()))
        }.addTo(compositeDisposable)
    }

    private fun loadBrandSlider() {
        childFragmentManager.beginTransaction().replace(R.id.brandsFragment, BrandSliderFragment.newInstance(
            onBrandClickSubject = onBrandClickSubject,
            onShowAllBrandClickSubject = onShowAllBrandClickSubject,
            viewStateSubject = viewStateBrandSliderSubject,
            onUpdateSubject  = onUpdateBrandSliderSubject
        )).commit()
    }

    //Слайдер стран
    private val viewStateCountrySliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateCountrySliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onCountryClickSubject: PublishSubject<Long> = PublishSubject.create()

    init {
        onCountryClickSubject.subscribeBy { countryId ->
            findNavController().navigate(HomeFragmentDirections.actionToPaginatedProductsCatalogWithoutFiltersFragment(
                PaginatedProductsCatalogWithoutFiltersFragment.DataSource.Country(countryId)
            ))
        }.addTo(compositeDisposable)
    }

    private fun loadCountrySlider() {
        childFragmentManager.beginTransaction().replace(R.id.countriesFragment, CountrySliderFragment.newInstance(
            onCountryClickSubject = onCountryClickSubject,
            viewStateSubject = viewStateCountrySliderSubject,
            onUpdateSubject  = onUpdateCountrySliderSubject
        )).commit()
    }

    //Слайдер просмотренных продуктов
    private val viewStateViewedSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateViewedSliderSubject: PublishSubject<Boolean> = PublishSubject.create()

    private fun loadViewedSlider() {
        childFragmentManager.beginTransaction().replace(R.id.viewedProductSliderFragment, ProductSliderFragment.newInstance(
            dataSource = ProductSliderFragment.DataSource.Request(
                sliderType = ProductSliderViewModel.VIEWED_PRODUCTS_SLIDER
            ),
            config = ProductSliderFragment.Config(false),
            viewStateSubject = viewStateViewedSliderSubject,
            onUpdateSubject  = onUpdateViewedSliderSubject,
            onProductClickSubject = onProductClickSubject,
            onShowAllClickSubject = onShowAllProductsSubject
        )).commit()
    }


    //Слайдер комментариев
    private val viewStateCommentSliderSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val onUpdateCommentSliderSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val onCommentClickSubject: PublishSubject<Long> = PublishSubject.create()

    private fun loadCommentSlider() {
        childFragmentManager.beginTransaction().replace(R.id.commentsSlider, CommentSliderFragment.newInstance(
            onCommentClickSubject =  onCommentClickSubject,
            viewStateSubject = viewStateCommentSliderSubject,
            onUpdateSubject  = onUpdateCommentSliderSubject
        )).commit()
    }

    //Дополнительная информация
    private fun loadAdditionInfoFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.additionalInfoFragment,
            AdditionalInfoSectionFragment()
        ).commit()
    }

    private fun loadFragments() {
        loadAdvertisingBannerSlider()
        loadHistorySlider()
        loadPopularCategorySlider()
        loadDiscountSlider()
        loadCategoryBannerSlider()
        loadTopSlider()
        loadOrderSlider()
        loadNoveltiesSlider()
        loadPromotionSlider()
        loadBottomSlider()
        loadBrandSlider()
        loadCountrySlider()
        loadViewedSlider()
        loadAdditionInfoFragment()
        loadCommentSlider()
    }

    private fun initSubjects() {
        viewStateAdvertisingBannerSliderSubject.subscribe { Log.i(LogSettings.DEVELOP_LOG, "ON SUB ${it::class.simpleName}") }
        Observable.zip(
            listOf(
                viewStateAdvertisingBannerSliderSubject,
                viewStateHistorySliderSubject,
                viewStatePopularCategorySliderSubject,
                viewStateDiscountSliderSubject,
                viewStateCategoryBannerSliderSubject,
                viewStateTopSliderSubject,
                viewStateOrderSliderSubject,
                viewStateNoveltiesSliderSubject,
                viewStatePromotionsSliderSubject,
                viewStateBottomSliderSubject,
                viewStateBrandSliderSubject,
                viewStateCountrySliderSubject,
                viewStateViewedSliderSubject,
                viewStateCommentSliderSubject
            )
        ) { viewStateList ->
            when(val errorState = viewStateList.find { it is ViewState.Error }) {
                null -> {
                    val firstState = viewStateList.first() as ViewState
                    if (firstState is ViewState.Hide || firstState is ViewState.Success) ViewState.Success()
                    else ViewState.Loading()
                }
                else -> errorState
            }
        }.flatMap { any -> Observable.just(any as ViewState) }.subscribeBy { state ->
            when(state) {
                is ViewState.Loading -> {
                    if (isFirstUpdate) {
                        onStateLoading()
                    }
                }
                is ViewState.Success -> {
                    isFirstUpdate = false
                    onStateSuccess()
                    binding.refreshContainer.isRefreshing = false
                }
                is ViewState.Error -> {
                    if (isFirstUpdate) {
                        onStateError(state.errorMessage)
                    } else {
                        binding.refreshContainer.isRefreshing = false
                        Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_SHORT).show()
                    }
                }
                is ViewState.Hide -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}