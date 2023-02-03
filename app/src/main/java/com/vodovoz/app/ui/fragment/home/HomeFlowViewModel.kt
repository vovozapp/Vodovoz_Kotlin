package com.vodovoz.app.ui.fragment.home

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.LocalSyncExtensions.syncCartQuantity
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.banner.AdvertisingBannersSliderResponseJsonParser.parseAdvertisingBannersSliderResponse
import com.vodovoz.app.data.parser.response.banner.CategoryBannersSliderResponseJsonParser.parseCategoryBannersSliderResponse
import com.vodovoz.app.data.parser.response.brand.BrandsSliderResponseJsonParser.parseBrandsSliderResponse
import com.vodovoz.app.data.parser.response.comment.CommentsSliderResponseJsonParser.parseCommentsSliderResponse
import com.vodovoz.app.data.parser.response.country.CountrySliderResponseJsonParser.parseCountriesSliderResponse
import com.vodovoz.app.data.parser.response.discount.DiscountSliderResponseParser.parseDiscountSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser.parseBottomSliderResponse
import com.vodovoz.app.data.parser.response.doubleSlider.DoubleSliderResponseJsonParser.parseTopSliderResponse
import com.vodovoz.app.data.parser.response.history.HistoriesSliderResponseJsonParser.parseHistoriesSliderResponse
import com.vodovoz.app.data.parser.response.novelties.NoveltiesSliderResponseParser.parseNoveltiesSliderResponse
import com.vodovoz.app.data.parser.response.order.OrderSliderResponseJsonParser.parseOrderSliderResponse
import com.vodovoz.app.data.parser.response.popular.PopularSliderResponseJsonParser.parsePopularSliderResponse
import com.vodovoz.app.data.parser.response.promotion.PromotionSliderResponseJsonParser.parsePromotionSliderResponse
import com.vodovoz.app.data.parser.response.viewed.ViewedProductSliderResponseJsonParser.parseViewedProductsSliderResponse
import com.vodovoz.app.mapper.BannerMapper.mapToUI
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import com.vodovoz.app.mapper.CountriesSliderBundleMapper.mapToUI
import com.vodovoz.app.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.base.PagingStateViewModel
import com.vodovoz.app.ui.base.State
import com.vodovoz.app.ui.base.toErrorState
import com.vodovoz.app.ui.fragment.home.adapter.Item
import com.vodovoz.app.ui.fragment.home.viewholders.homebanners.HomeBanners
import com.vodovoz.app.ui.fragment.home.viewholders.homebottominfo.HomeBottomInfo
import com.vodovoz.app.ui.fragment.home.viewholders.homebrands.HomeBrands
import com.vodovoz.app.ui.fragment.home.viewholders.homecomments.HomeComments
import com.vodovoz.app.ui.fragment.home.viewholders.homecountries.HomeCountries
import com.vodovoz.app.ui.fragment.home.viewholders.homehistories.HomeHistories
import com.vodovoz.app.ui.fragment.home.viewholders.homeorders.HomeOrders
import com.vodovoz.app.ui.fragment.home.viewholders.homepopulars.HomePopulars
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.BOTTOM_PROD
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.NOVELTIES
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.TOP_PROD
import com.vodovoz.app.ui.fragment.home.viewholders.homeproducts.HomeProducts.Companion.VIEWED
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.ui.fragment.home.viewholders.hometriplenav.HomeTripleNav
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository
) : PagingStateViewModel<HomeFlowViewModel.HomeState>(HomeState.idle()) {

    private fun loadPage() {
        fetchAdvertisingBannersSlider()
        fetchHistoriesSlider()
        fetchPopularSlider()
        fetchDiscountsSlider()
        fetchCategoryBannersSlider()
        fetchTopSlider()
        fetchOrdersSlider()
        fetchNoveltiesSlider()
        fetchPromotionsSlider()
        fetchBottomSlider()
        fetchBrandsSlider()
        fetchCountriesSlider()
        fetchViewedProductsSlider()
        fetchCommentsSlider()
    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            loadPage()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        loadPage()
    }

    private fun fetchAdvertisingBannersSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchAdvertisingBannersSlider()) }
                .catch {
                    debugLog { "fetch adv banners error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(),
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_1,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseAdvertisingBannersSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item = PositionItem(
                            POSITION_1,
                            HomeBanners(1, response.data.mapToUI(), bannerRatio = 0.41)
                        )
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_1,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchHistoriesSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchHistoriesSlider()) }
                .catch {
                    debugLog { "fetch histories error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_2,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseHistoriesSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_2, HomeHistories(2, response.data.mapToUI()))
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_2,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchPopularSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchPopularSlider()) }
                .catch {
                    debugLog { "fetch populars error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_3,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePopularSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_3, HomePopulars(3, response.data.mapToUI()))
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_3,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchDiscountsSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchDiscountsSlider()) }
                .catch {
                    debugLog { "fetch discount slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_4,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseDiscountSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_4, HomeProducts(4, response.data.mapToUI(), productsType = DISCOUNT, productsSliderConfig = ProductsSliderConfig(
                                containShowAllButton = true
                            )))

                        response.data.forEach { categoryDetailEntity ->
                            categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                        }

                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_4,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchCategoryBannersSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchCategoryBannersSlider()) }
                .catch {
                    debugLog { "fetch category banners error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_5,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCategoryBannersSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item = PositionItem(
                            POSITION_5,
                            HomeBanners(5, response.data.mapToUI(), bannerRatio = 0.5)
                        )
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_5,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchTopSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchTopSlider()) }
                .catch {
                    debugLog { "fetch top slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_6,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseTopSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_6, HomeProducts(6, response.data.mapToUI(), productsType = TOP_PROD, productsSliderConfig = ProductsSliderConfig(
                                containShowAllButton = true
                            )))

                        response.data.forEach { categoryDetailEntity ->
                            categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                        }

                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_6,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchOrdersSlider() {
        viewModelScope.launch {
            val userId = localDataSource.fetchUserId()
            if (userId != null) {
                flow { emit(repository.fetchOrdersSlider(userId)) }
                    .catch {
                        debugLog { "fetch orders slider error ${it.localizedMessage}" }
                        uiStateListener.value =
                            state.copy(
                                error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                    items = state.data.items + PositionItem(
                                        POSITION_7,
                                        null
                                    )
                                )
                            )
                    }
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        val response = it.parseOrderSliderResponse()
                        uiStateListener.value = if (response is ResponseEntity.Success) {
                            val item =
                                PositionItem(POSITION_7, HomeOrders(7, response.data.mapToUI()))
                            state.copy(
                                loadingPage = false,
                                data = state.data.copy(items = state.data.items + item),
                                error = null
                            )
                        } else {
                            state.copy(
                                loadingPage = false, data = state.data.copy(
                                    items = state.data.items + PositionItem(
                                        POSITION_7,
                                        null
                                    )
                                )
                            )
                        }
                    }
                    .flowOn(Dispatchers.Default)
                    .collect()
            } else {
                uiStateListener.value = state.copy(
                    loadingPage = false, data = state.data.copy(
                        items = state.data.items + PositionItem(
                            POSITION_7,
                            null
                        )
                    )
                )
            }
        }
    }

    private fun fetchNoveltiesSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchNoveltiesSlider()) }
                .catch {
                    debugLog { "fetch novelties error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_9,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseNoveltiesSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_9, HomeProducts(9, response.data.mapToUI(), productsType = NOVELTIES, productsSliderConfig = ProductsSliderConfig(
                                containShowAllButton = true
                            )))

                        response.data.forEach { categoryDetailEntity ->
                            categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                        }

                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_9,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchPromotionsSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchPromotionsSlider()) }
                .catch {
                    debugLog { "fetch promotions slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_10,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePromotionSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item = PositionItem(
                            POSITION_10, HomePromotions(
                                10, PromotionsSliderBundleUI(
                                    title = "Акции",
                                    containShowAllButton = true,
                                    promotionUIList = response.data.mapToUI()
                                )
                            )
                        )

                        response.data.forEach { promotionEntity ->
                            promotionEntity.productEntityList.syncFavoriteProducts(localDataSource)
                            promotionEntity.productEntityList.syncCartQuantity(localDataSource)
                        }

                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_10,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchBottomSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchBottomSlider()) }
                .catch {
                    debugLog { "fetch bottom slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_11,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseBottomSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_11, HomeProducts(11, response.data.mapToUI(), productsType = BOTTOM_PROD, productsSliderConfig = ProductsSliderConfig(
                                containShowAllButton = true
                            )))

                        response.data.forEach { categoryDetailEntity ->
                            categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                        }

                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_11,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchBrandsSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchBrandsSlider()) }
                .catch {
                    debugLog { "fetch brands slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_12,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseBrandsSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_12, HomeBrands(12, response.data.mapToUI()))
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_12,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchCountriesSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchCountriesSlider()) }
                .catch {
                    debugLog { "fetch countries slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_13,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCountriesSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_13, HomeCountries(13, response.data.mapToUI()))
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_13,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchViewedProductsSlider() {
        viewModelScope.launch {
            val userId = localDataSource.fetchUserId()
            if (userId != null) {
                flow { emit(repository.fetchViewedProductsSlider(userId)) }
                    .catch {
                        debugLog { "fetch viewed products slider error ${it.localizedMessage}" }
                        uiStateListener.value =
                            state.copy(
                                error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                    items = state.data.items + PositionItem(
                                        POSITION_14,
                                        null
                                    )
                                )
                            )
                    }
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        val response = it.parseViewedProductsSliderResponse()
                        uiStateListener.value = if (response is ResponseEntity.Success) {
                            val item = PositionItem(
                                POSITION_14, HomeProducts(
                                    14, response.data.mapToUI(), ProductsSliderConfig(
                                        containShowAllButton = false
                                    ), VIEWED
                                )
                            )

                            response.data.forEach { categoryDetailEntity ->
                                categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                    localDataSource
                                )
                                categoryDetailEntity.productEntityList.syncCartQuantity(
                                    localDataSource
                                )
                            }

                            state.copy(
                                loadingPage = false,
                                data = state.data.copy(items = state.data.items + item),
                                error = null
                            )
                        } else {
                            state.copy(
                                loadingPage = false, data = state.data.copy(
                                    items = state.data.items + PositionItem(
                                        POSITION_14,
                                        null
                                    )
                                )
                            )
                        }
                    }
                    .flowOn(Dispatchers.Default)
                    .collect()
            } else {
                uiStateListener.value = state.copy(
                    loadingPage = false, data = state.data.copy(
                        items = state.data.items + PositionItem(
                            POSITION_14,
                            null
                        )
                    )
                )
            }
        }
    }

    private fun fetchCommentsSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchCommentsSlider()) }
                .catch {
                    debugLog { "fetch comments slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(
                            error = it.toErrorState(), loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_15,
                                    null
                                )
                            )
                        )
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCommentsSliderResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val item =
                            PositionItem(POSITION_15, HomeComments(15, response.data.mapToUI()))
                        state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = state.data.items + item),
                            error = null
                        )
                    } else {
                        state.copy(
                            loadingPage = false, data = state.data.copy(
                                items = state.data.items + PositionItem(
                                    POSITION_15,
                                    null
                                )
                            )
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun isLoginAlready() = dataRepository.isAlreadyLogin()

    fun changeCart(productId: Long, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository
                .changeCart(
                    productId = productId,
                    quantity = quantity
                )
        }
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            when(isFavorite) {
                true -> dataRepository.addToFavorite(productId)
                false -> dataRepository.removeFromFavorite(productId = productId)

            }
        }
    }

    data class PositionItem(
        val position: Int,
        val item: Item?
    )

    data class HomeState(
        val items: List<PositionItem>
    ) : State {

        companion object {
            fun idle(): HomeState {
                return HomeState(
                    listOf(
                        PositionItem(POSITION_8, HomeTripleNav(8)),
                        PositionItem(POSITION_16, HomeBottomInfo(16))
                    )
                )
            }
        }
    }

    companion object {
        const val POSITION_1 = 1
        const val POSITION_2 = 2
        const val POSITION_3 = 3
        const val POSITION_4 = 4
        const val POSITION_5 = 5
        const val POSITION_6 = 6
        const val POSITION_7 = 7
        const val POSITION_8 = 8
        const val POSITION_9 = 9
        const val POSITION_10 = 10
        const val POSITION_11 = 11
        const val POSITION_12 = 12
        const val POSITION_13 = 13
        const val POSITION_14 = 14
        const val POSITION_15 = 15
        const val POSITION_16 = 16

        const val POSITIONS_COUNT = 16
    }
}