package com.vodovoz.app.ui.fragment.home

import androidx.lifecycle.viewModelScope
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
import com.vodovoz.app.ui.base.PagingState.Companion.idle
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
import com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.ui.fragment.home.viewholders.hometriplenav.HomeTripleNav
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource
) : PagingStateViewModel<HomeFlowViewModel.HomeState>(HomeState.idle()) {

    fun loadPage() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true)
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
    }

    private fun fetchAdvertisingBannersSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchAdvertisingBannersSlider()) }
                .catch {
                    debugLog { "fetch adv banners error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseAdvertisingBannersSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(1, HomeBanners(1, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseHistoriesSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(2, HomeHistories(2, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePopularSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(3, HomePopulars(3, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseDiscountSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(4, HomeProducts(4, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
                        )

                        response.data.forEach { categoryDetailEntity ->
                            categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                        }
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCategoryBannersSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(5, HomeBanners(5, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseTopSliderResponse()
                    if (response is ResponseEntity.Success) {

                        val item = PeriodItem(6, HomeProducts(6, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
                        )

                        response.data.forEach { categoryDetailEntity ->
                            categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                        }
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
                            state.copy(error = it.toErrorState(), loadingPage = false)
                    }
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        val response = it.parseOrderSliderResponse()
                        if (response is ResponseEntity.Success) {
                            val item = PeriodItem(7, HomeOrders(7, response.data.mapToUI()))
                            uiStateListener.value = state.copy(
                                loadingPage = false,
                                data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                                error = null
                            )
                        }
                    }
                    .flowOn(Dispatchers.Default)
                    .collect()
            }
        }
    }

    private fun fetchNoveltiesSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchNoveltiesSlider()) }
                .catch {
                    debugLog { "fetch novelties error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseNoveltiesSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(9, HomeProducts(9, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
                        )

                        response.data.forEach { categoryDetailEntity ->
                            categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                        }
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePromotionSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(
                            10, HomePromotions(
                                10, PromotionsSliderBundleUI(
                                    title = "Акции",
                                    containShowAllButton = true,
                                    promotionUIList = response.data.mapToUI()
                                )
                            )
                        )
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
                        )

                        response.data.forEach { promotionEntity ->
                            promotionEntity.productEntityList.syncFavoriteProducts(localDataSource)
                            promotionEntity.productEntityList.syncCartQuantity(localDataSource)
                        }
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseBottomSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(11, HomeProducts(11, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
                        )

                        response.data.forEach { categoryDetailEntity ->
                            categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            categoryDetailEntity.productEntityList.syncCartQuantity(localDataSource)
                        }
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseBrandsSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(12, HomeBrands(12, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
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
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCountriesSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(13, HomeCountries(13, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
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
                            state.copy(error = it.toErrorState(), loadingPage = false)
                    }
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        val response = it.parseViewedProductsSliderResponse()
                        if (response is ResponseEntity.Success) {
                            val item = PeriodItem(14, HomeProducts(14, response.data.mapToUI()))
                            uiStateListener.value = state.copy(
                                loadingPage = false,
                                data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                                error = null
                            )

                            response.data.forEach { categoryDetailEntity ->
                                categoryDetailEntity.productEntityList.syncFavoriteProducts(
                                    localDataSource
                                )
                                categoryDetailEntity.productEntityList.syncCartQuantity(
                                    localDataSource
                                )
                            }
                        }
                    }
                    .flowOn(Dispatchers.Default)
                    .collect()
            }
        }
    }

    private fun fetchCommentsSlider() {
        viewModelScope.launch {
            flow { emit(repository.fetchCommentsSlider()) }
                .catch {
                    debugLog { "fetch comments slider error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCommentsSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val item = PeriodItem(15, HomeComments(15, response.data.mapToUI()))
                        uiStateListener.value = state.copy(
                            loadingPage = false,
                            data = state.data.copy(items = (state.data.items + item).sortedBy { item.id }),
                            error = null
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    data class PeriodItem(
        val id: Int,
        val item: Item
    )

    data class HomeState(
        val items: List<PeriodItem>
    ) : State {

        companion object {
            fun idle(): HomeState {
                return HomeState(
                    listOf(
                        PeriodItem(8, HomeTripleNav(8)),
                        PeriodItem(16, HomeBottomInfo(16))
                    )
                )
            }
        }
    }
}