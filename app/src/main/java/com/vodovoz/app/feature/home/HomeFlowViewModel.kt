package com.vodovoz.app.feature.home

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.DataRepository
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
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.parser.response.popupNews.PopupNewsResponseJsonParser.parsePopupNewsResponse
import com.vodovoz.app.feature.home.viewholders.homebanners.HomeBanners
import com.vodovoz.app.feature.home.viewholders.homebottominfo.HomeBottomInfo
import com.vodovoz.app.feature.home.viewholders.homebrands.HomeBrands
import com.vodovoz.app.feature.home.viewholders.homecomments.HomeComments
import com.vodovoz.app.feature.home.viewholders.homecountries.HomeCountries
import com.vodovoz.app.feature.home.viewholders.homehistories.HomeHistories
import com.vodovoz.app.feature.home.viewholders.homeorders.HomeOrders
import com.vodovoz.app.feature.home.viewholders.homepopulars.HomePopulars
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.BOTTOM_PROD
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.DISCOUNT
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.NOVELTIES
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.TOP_PROD
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts.Companion.VIEWED
import com.vodovoz.app.feature.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.feature.home.viewholders.hometriplenav.HomeTripleNav
import com.vodovoz.app.mapper.PopupNewsMapper.mapToUI
import com.vodovoz.app.ui.fragment.slider.products_slider.ProductsSliderConfig
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager
) : PagingContractViewModel<HomeFlowViewModel.HomeState, HomeFlowViewModel.HomeEvents>(HomeState.idle()) {

    private fun loadPage() {
        updatePopupNews()
        fetchAdvertisingBannersSlider(POSITION_1)
        fetchHistoriesSlider(POSITION_2)
        fetchPopularSlider(POSITION_3)
        fetchDiscountsSlider(POSITION_4)
        fetchCategoryBannersSlider(POSITION_5)
    }

    fun secondLoad() {
        fetchTopSlider(POSITION_6)
        fetchOrdersSlider(POSITION_7)
        fetchNoveltiesSlider(POSITION_9)
        fetchPromotionsSlider(POSITION_10)
        fetchBottomSlider(POSITION_11)
        fetchBrandsSlider(POSITION_12)
        fetchCountriesSlider(POSITION_13)
        fetchViewedProductsSlider(POSITION_14)
        fetchCommentsSlider(POSITION_15)
    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            loadPage()
        }
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(loadingPage = true, data = state.data.copy(items = HomeState.idle().items, itemsInt = HomeState.idle().itemsInt))
        loadPage()
        secondLoad()
    }

    private fun fetchAdvertisingBannersSlider(position: Int) {
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.fetchAdvertisingBannersSlider()) }
                .catch {
                    debugLog { "fetch adv banners error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseAdvertisingBannersSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, HomeBanners(position, response.data.mapToUI(), bannerRatio = 0.41))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchHistoriesSlider(position: Int) {
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.fetchHistoriesSlider()) }
                .catch {
                    debugLog { "fetch histories error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseHistoriesSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, HomeHistories(position, response.data.mapToUI()))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchPopularSlider(position: Int) {
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.fetchPopularSlider()) }
                .catch {
                    debugLog { "fetch populars error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePopularSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, HomePopulars(position, response.data.mapToUI()))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchDiscountsSlider(position: Int) {
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.fetchDiscountsSlider()) }
                .catch {
                    debugLog { "fetch discount slider error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseDiscountSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, fetchHomeProductsByType(response.data.mapToUI(), DISCOUNT, position))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchCategoryBannersSlider(position: Int) {
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.fetchCategoryBannersSlider()) }
                .catch {
                    debugLog { "fetch category banners error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCategoryBannersSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, HomeBanners(position, response.data.mapToUI(), bannerRatio = 0.5))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchTopSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_6" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            flow { emit(repository.fetchTopSlider()) }
                .catch {
                    debugLog { "fetch top slider error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseTopSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, fetchHomeProductsByType(response.data.mapToUI(), TOP_PROD, position))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchOrdersSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_7" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            val userId = localDataSource.fetchUserId()
            if (userId != null) {
                flow { emit(repository.fetchOrdersSlider(userId)) }
                    .catch {
                        debugLog { "fetch orders slider error ${it.localizedMessage}" }
                        updateStateByThrowableAndPosition(it, position)
                    }
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        val response = it.parseOrderSliderResponse()
                        val item = if (response is ResponseEntity.Success) {
                            PositionItem(position, HomeOrders(position, response.data.mapToUI()))
                        } else {
                            PositionItem(position, null)
                        }
                        updateStateByPositionItem(item)
                    }
                    .flowOn(Dispatchers.Default)
                    .collect()
            } else {
                updateStateByPositionItem(PositionItem(position, null))
            }
        }
    }

    private fun fetchNoveltiesSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_9" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            flow { emit(repository.fetchNoveltiesSlider()) }
                .catch {
                    debugLog { "fetch novelties error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseNoveltiesSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, fetchHomeProductsByType(response.data.mapToUI(), NOVELTIES, position))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchPromotionsSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_10" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            flow { emit(repository.fetchPromotionsSlider()) }
                .catch {
                    debugLog { "fetch promotions slider error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePromotionSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(
                            position, HomePromotions(
                                position, PromotionsSliderBundleUI(
                                    title = "Акции",
                                    containShowAllButton = true,
                                    promotionUIList = response.data.mapToUI()
                                )
                            )
                        )
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchBottomSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_11" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            flow { emit(repository.fetchBottomSlider()) }
                .catch {
                    debugLog { "fetch bottom slider error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseBottomSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, fetchHomeProductsByType(response.data.mapToUI(), BOTTOM_PROD, position))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchBrandsSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_12" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            flow { emit(repository.fetchBrandsSlider()) }
                .catch {
                    debugLog { "fetch brands slider error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseBrandsSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, HomeBrands(position, response.data.mapToUI()))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchCountriesSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_13" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            flow { emit(repository.fetchCountriesSlider()) }
                .catch {
                    debugLog { "fetch countries slider error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCountriesSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, HomeCountries(position, response.data.mapToUI()))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchViewedProductsSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_14" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            val userId = localDataSource.fetchUserId()
            if (userId != null) {
                flow { emit(repository.fetchViewedProductsSlider(userId)) }
                    .catch {
                        debugLog { "fetch viewed products slider error ${it.localizedMessage}" }
                        updateStateByThrowableAndPosition(it, position)
                    }
                    .flowOn(Dispatchers.IO)
                    .onEach {
                        val response = it.parseViewedProductsSliderResponse()
                        val item = if (response is ResponseEntity.Success) {
                            PositionItem(position, fetchHomeProductsByType(response.data.mapToUI(), VIEWED, position))
                        } else {
                            PositionItem(position, null)
                        }
                        updateStateByPositionItem(item)
                    }
                    .flowOn(Dispatchers.Default)
                    .collect()
            } else {
                updateStateByPositionItem(PositionItem(POSITION_14, null))
            }
        }
    }

    private fun fetchCommentsSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load POSITION_15" }
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(itemsInt = state.data.itemsInt + position))
        viewModelScope.launch {
            flow { emit(repository.fetchCommentsSlider()) }
                .catch {
                    debugLog { "fetch comments slider error ${it.localizedMessage}" }
                    updateStateByThrowableAndPosition(it, position)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCommentsSliderResponse()
                    val item = if (response is ResponseEntity.Success) {
                        PositionItem(position, HomeComments(position, response.data.mapToUI()))
                    } else {
                        PositionItem(position, null)
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun updatePopupNews() {
        viewModelScope.launch {
            flow { emit(repository.fetchPopupNews(localDataSource.fetchUserId()))  }
                .catch { debugLog { "fetch popup news error ${it.localizedMessage}" } }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parsePopupNewsResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        state.copy(
                            loadingPage = false,
                            error = null,
                            data = state.data.copy(
                                news = data
                            )
                        )
                    } else {
                        state.copy(
                            loadingPage = false
                        )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun updateStateByPositionItem(positionItem: PositionItem) {
        uiStateListener.value = state.copy(
            loadingPage = false,
            data = state.data.copy(items = state.data.items + positionItem),
            error = null
        )
    }

    private fun updateStateByThrowableAndPosition(it: Throwable, position: Int) {
        uiStateListener.value =
            state.copy(
                error = it.toErrorState(),
                loadingPage = false,
                data = state.data.copy(
                    items = state.data.items + PositionItem(
                        position,
                        null
                    )
                )
            )
    }

    private fun fetchHomeProductsByType(data: List<CategoryDetailUI>, type: Int, position: Int): HomeProducts {
        return HomeProducts(
            position,
            data,
            productsType = type,
            productsSliderConfig = ProductsSliderConfig(
                containShowAllButton = true
            ),
            prodList =  if (data.size > 1) {
                val list = mutableListOf<ProductUI>()
                data.forEach {
                    list.addAll(it.productUIList)
                }
                list
            } else {
                data.first().productUIList
            }
        )
    }

    fun onPreOrderClick(id: Long, name: String, detailPicture: String) {
        viewModelScope.launch {
            val accountId = accountManager.fetchAccountId()
            if (accountId == null) {
                //eventListener.emit(HomeEvents.GoToProfile)
                eventListener.emit(HomeEvents.GoToPreOrder(id, name, detailPicture))
            } else {
                eventListener.emit(HomeEvents.GoToPreOrder(id, name, detailPicture))
            }
        }
    }

    fun onSendCommentClick() {
        viewModelScope.launch {
            val accountId = accountManager.fetchAccountId()
            if (accountId == null) {
                eventListener.emit(HomeEvents.GoToProfile)
            } else {
                eventListener.emit(HomeEvents.SendComment)
            }
        }
    }

    fun isLoginAlready() = dataRepository.isAlreadyLogin()

    fun changeCart(productId: Long, quantity: Int, oldQuan: Int) {
        viewModelScope.launch {
            cartManager.add(id = productId, oldCount = oldQuan, newCount = quantity)
        }
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            likeManager.like(productId, !isFavorite)
        }
    }

    fun changeRating(productId: Long, rating: Float, oldRating: Float) {
        viewModelScope.launch {
            ratingProductManager.rate(productId, rating = rating, oldRating = oldRating)
        }
    }

    fun hasShown() {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                hasShow = true
            )
        )
    }

    data class PositionItem(
        val position: Int,
        val item: Item?
    )

    sealed class HomeEvents : Event {
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) : HomeEvents()
        object GoToProfile : HomeEvents()
        object SendComment : HomeEvents()
    }

    data class HomeState(
        val items: List<PositionItem>,
        val itemsInt: List<Int>,
        val news: PopupNewsUI? = null,
        val hasShow: Boolean = false
    ) : State {

        companion object {
            fun idle(): HomeState {
                return HomeState(
                    listOf(
                        PositionItem(POSITION_8, HomeTripleNav(POSITION_8)),
                        PositionItem(POSITION_16, HomeBottomInfo(POSITION_16))
                    ),
                    itemsInt = listOf(POSITION_8, POSITION_16)
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