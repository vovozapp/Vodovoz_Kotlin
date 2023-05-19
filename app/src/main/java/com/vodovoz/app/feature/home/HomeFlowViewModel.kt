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
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeProductsTabs
import com.vodovoz.app.feature.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.BRANDS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.COMMENTS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.DISCOUNT_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.HISTORIES_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.NOVELTIES_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.ORDERS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.POPULARS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.PROMOTIONS_TITLE
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle.Companion.VIEWED_TITLE
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
    private val accountManager: AccountManager,
) : PagingContractViewModel<HomeFlowViewModel.HomeState, HomeFlowViewModel.HomeEvents>(HomeState.idle()) {

    private fun loadPage() {
        updatePopupNews()
        fetchAdvertisingBannersSlider(POSITION_1)
        fetchHistoriesSlider(POSITION_2_TITLE, POSITION_3)
        fetchPopularSlider(POSITION_4_TITLE, POSITION_5)
        fetchDiscountsSlider(POSITION_6_TITLE, POSITION_7)
        fetchCategoryBannersSlider(POSITION_8)
    }

    fun secondLoad() {
        fetchTopSlider(POSITION_9_TAB, POSITION_10)
        fetchOrdersSlider(POSITION_11_TITLE, POSITION_12)
        fetchNoveltiesSlider(POSITION_14_TITLE, POSITION_15)
        fetchPromotionsSlider(POSITION_16_TITLE, POSITION_17)
        fetchBottomSlider(POSITION_18_TAB, POSITION_19)
        fetchBrandsSlider(POSITION_20_TITLE, POSITION_21)
        fetchCountriesSlider(POSITION_22)
        fetchViewedProductsSlider(POSITION_23_TITLE, POSITION_24)
        fetchCommentsSlider(POSITION_25_TITLE, POSITION_26)
    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            loadPage()
        }
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(
                loadingPage = true,
                data = state.data.copy(
                    items = HomeState.idle().items,
                    itemsInt = HomeState.idle().itemsInt
                )
            )
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
                        PositionItem(
                            position,
                            HomeBanners(position, response.data.mapToUI(), bannerRatio = 0.41)
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

    private fun fetchHistoriesSlider(positionTitle: Int, position: Int) {
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
                        PositionItemWithTitle(
                            item = PositionItem(
                                position,
                                HomeHistories(position, response.data.mapToUI())
                            ),
                            itemTitle = PositionItem(
                                positionTitle,
                                HomeTitle(
                                    id = positionTitle,
                                    type = HISTORIES_TITLE,
                                    name = "Истории"
                                )
                            )
                        )
                    } else {
                        PositionItemWithTitle(
                            item = PositionItem(position, null),
                            itemTitle = PositionItem(positionTitle, null)
                        )
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchPopularSlider(positionTitle: Int, position: Int) {
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
                        PositionItemWithTitle(
                            item = PositionItem(
                                position,
                                HomePopulars(position, response.data.mapToUI())
                            ),
                            itemTitle = PositionItem(
                                positionTitle,
                                HomeTitle(
                                    id = positionTitle,
                                    type = POPULARS_TITLE,
                                    name = "Популярные разделы"
                                )
                            )
                        )
                    } else {
                        PositionItemWithTitle(
                            item = PositionItem(position, null),
                            itemTitle = PositionItem(positionTitle, null)
                        )
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchDiscountsSlider(positionTitle: Int, position: Int) {
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
                        val data = response.data.mapToUI()
                        PositionItemWithTitle(
                            item = PositionItem(
                                position,
                                fetchHomeProductsByType(data, DISCOUNT, position)
                            ),
                            itemTitle = PositionItem(
                                positionTitle,
                                HomeTitle(
                                    id = positionTitle,
                                    type = DISCOUNT_TITLE,
                                    name = "Самое выгодное",
                                    showAll = true,
                                    showAllName = "СМ.ВСЕ",
                                    categoryProductsName = if (data.size == 1) {
                                        data.first().name
                                    } else {
                                        ""
                                    }
                                )
                            )
                        )
                    } else {
                        PositionItemWithTitle(
                            item = PositionItem(position, null),
                            itemTitle = PositionItem(positionTitle, null)
                        )
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
                        PositionItem(
                            position,
                            HomeBanners(position, response.data.mapToUI(), bannerRatio = 0.5)
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

    private fun fetchTopSlider(positionTab: Int, position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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
                        val data = response.data.mapToUI()
                        PositionItemWithTabs(
                            item = PositionItem(
                                position,
                                fetchHomeProductsByType(response.data.mapToUI(), TOP_PROD, position)
                            ),
                            itemTabs = PositionItem(
                                positionTab,
                                HomeProductsTabs(id = positionTab, data.mapIndexed { index, cat ->
                                    if (index == 0) {
                                        cat.copy(isSelected = true, position = positionTab)
                                    } else {
                                        cat.copy(isSelected = false, position = positionTab)
                                    }
                                })
                            )
                        )
                    } else {
                        PositionItemWithTabs(
                            item = PositionItem(position, null),
                            itemTabs = PositionItem(positionTab, null)
                        )
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchOrdersSlider(positionTitle: Int, position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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
                            PositionItemWithTitle(
                                item = PositionItem(
                                    position,
                                    HomeOrders(position, response.data.mapToUI())
                                ),
                                itemTitle = PositionItem(
                                    positionTitle,
                                    HomeTitle(
                                        id = positionTitle,
                                        type = ORDERS_TITLE,
                                        name = "Мои заказы",
                                        showAll = true,
                                        showAllName = "СМ.ВСЕ"
                                    )
                                )
                            )
                        } else {
                            PositionItemWithTitle(
                                item = PositionItem(position, null),
                                itemTitle = PositionItem(positionTitle, null)
                            )
                        }
                        updateStateByPositionItem(item)
                    }
                    .flowOn(Dispatchers.Default)
                    .collect()
            } else {
                updateStateByPositionItem(
                    PositionItemWithTitle(
                        item = PositionItem(position, null),
                        itemTitle = PositionItem(positionTitle, null)
                    )
                )
            }
        }
    }

    private fun fetchNoveltiesSlider(positionTitle: Int, position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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
                        val data = response.data.mapToUI()
                        PositionItemWithTitle(
                            item = PositionItem(
                                position,
                                fetchHomeProductsByType(data, NOVELTIES, position)
                            ),
                            itemTitle = PositionItem(
                                positionTitle,
                                HomeTitle(
                                    id = positionTitle,
                                    type = NOVELTIES_TITLE,
                                    name = "Новинки",
                                    showAll = true,
                                    showAllName = "СМ.ВСЕ",
                                    categoryProductsName = if (data.size == 1) {
                                        data.first().name
                                    } else {
                                        ""
                                    }
                                )
                            )
                        )
                    } else {
                        PositionItemWithTitle(
                            item = PositionItem(position, null),
                            itemTitle = PositionItem(positionTitle, null)
                        )
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchPromotionsSlider(positionTitle: Int, position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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
                        PositionItemWithTitle(
                            item = PositionItem(
                                position, HomePromotions(
                                    position, PromotionsSliderBundleUI(
                                        title = "Акции",
                                        containShowAllButton = true,
                                        promotionUIList = response.data.mapToUI()
                                    )
                                )
                            ),
                            itemTitle = PositionItem(
                                positionTitle,
                                HomeTitle(
                                    id = positionTitle,
                                    type = PROMOTIONS_TITLE,
                                    name = "Акции",
                                    showAll = true,
                                    showAllName = "СМ.ВСЕ",
                                    lightBg = false
                                )
                            )
                        )
                    } else {
                        PositionItemWithTitle(
                            item = PositionItem(position, null),
                            itemTitle = PositionItem(positionTitle, null)
                        )
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchBottomSlider(positionTab: Int, position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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
                        val data = response.data.mapToUI()
                        PositionItemWithTabs(
                            item = PositionItem(
                                position,
                                fetchHomeProductsByType(
                                    response.data.mapToUI(),
                                    BOTTOM_PROD,
                                    position
                                )
                            ),
                            itemTabs = PositionItem(
                                positionTab,
                                HomeProductsTabs(id = positionTab, data.mapIndexed { index, cat ->
                                    if (index == 0) {
                                        cat.copy(isSelected = true, position = positionTab)
                                    } else {
                                        cat.copy(isSelected = false, position = positionTab)
                                    }
                                })
                            )
                        )
                    } else {
                        PositionItemWithTabs(
                            item = PositionItem(position, null),
                            itemTabs = PositionItem(positionTab, null)
                        )
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchBrandsSlider(positionTitle: Int, position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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
                        PositionItemWithTitle(
                            item = PositionItem(
                                position,
                                HomeBrands(position, response.data.mapToUI())
                            ),
                            itemTitle = PositionItem(
                                positionTitle,
                                HomeTitle(
                                    id = positionTitle,
                                    type = BRANDS_TITLE,
                                    name = "Бренды",
                                    showAll = true,
                                    showAllName = "СМ.ВСЕ"
                                )
                            )
                        )
                    } else {
                        PositionItemWithTitle(
                            item = PositionItem(position, null),
                            itemTitle = PositionItem(positionTitle, null)
                        )
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun fetchCountriesSlider(position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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

    private fun fetchViewedProductsSlider(positionTitle: Int, position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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
                            val data = response.data.mapToUI()
                            PositionItemWithTitle(
                                item = PositionItem(
                                    position,
                                    fetchHomeProductsByType(data, VIEWED, position)
                                ),
                                itemTitle = PositionItem(
                                    positionTitle,
                                    HomeTitle(
                                        id = positionTitle,
                                        type = VIEWED_TITLE,
                                        name = "Вы смотрели",
                                        showAll = true,
                                        showAllName = "СМ.ВСЕ",
                                        categoryProductsName = if (data.size == 1) {
                                            data.first().name
                                        } else {
                                            ""
                                        }
                                    )
                                )
                            )
                        } else {
                            PositionItemWithTitle(
                                item = PositionItem(position, null),
                                itemTitle = PositionItem(positionTitle, null)
                            )
                        }
                        updateStateByPositionItem(item)
                    }
                    .flowOn(Dispatchers.Default)
                    .collect()
            } else {
                updateStateByPositionItem(
                    PositionItemWithTitle(
                        item = PositionItem(position, null),
                        itemTitle = PositionItem(positionTitle, null)
                    )
                )
            }
        }
    }

    private fun fetchCommentsSlider(positionTitle: Int, position: Int) {
        if (state.data.itemsInt.contains(position)) return
        debugLog { "load $position" }
        uiStateListener.value = state.copy(
            loadingPage = true,
            data = state.data.copy(itemsInt = state.data.itemsInt + position)
        )
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
                        PositionItemWithTitle(
                            item = PositionItem(
                                position,
                                HomeComments(position, response.data.mapToUI())
                            ),
                            itemTitle = PositionItem(
                                positionTitle,
                                HomeTitle(
                                    id = positionTitle,
                                    type = COMMENTS_TITLE,
                                    name = "Отзывы",
                                    showAll = true,
                                    showAllName = "Написать отзыв"
                                )
                            )
                        )
                    } else {
                        PositionItemWithTitle(
                            item = PositionItem(position, null),
                            itemTitle = PositionItem(positionTitle, null)
                        )
                    }
                    updateStateByPositionItem(item)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun updatePopupNews() {
        viewModelScope.launch {
            flow { emit(repository.fetchPopupNews(localDataSource.fetchUserId())) }
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

    private fun updateStateByPositionItem(positionItemWithTitle: PositionItemWithTitle) {
        uiStateListener.value = state.copy(
            loadingPage = false,
            data = state.data.copy(items = state.data.items + positionItemWithTitle.itemTitle + positionItemWithTitle.item),
            error = null
        )
    }

    private fun updateStateByPositionItem(positionItemWithTabs: PositionItemWithTabs) {
        uiStateListener.value = state.copy(
            loadingPage = false,
            data = state.data.copy(items = state.data.items + positionItemWithTabs.itemTabs + positionItemWithTabs.item),
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

    private fun fetchHomeProductsByType(
        data: List<CategoryDetailUI>,
        type: Int,
        position: Int,
    ): HomeProducts {
        return HomeProducts(
            position,
            data,
            productsType = type,
            productsSliderConfig = ProductsSliderConfig(
                containShowAllButton = true
            ),
            prodList = data.first().productUIList
        )
    }

    private fun updateStateByTabAndProductPositions(positionTab:Int, position: Int, categoryId: Long) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                items = state.data.items.map {
                    when (it.position) {
                        position -> {
                            it.copy(
                                item = (it.item as HomeProducts).copy(
                                    prodList = it.item.items.find { it.id == categoryId }?.productUIList
                                        ?: it.item.prodList
                                )
                            )
                        }

                        positionTab -> {
                            it.copy(
                                item = (it.item as HomeProductsTabs).copy(
                                    tabsNames = it.item.tabsNames.map{cat ->
                                        if (cat.id == categoryId) {
                                            cat.copy(isSelected = true)
                                        } else {
                                            cat.copy(isSelected = false)
                                        }
                                    }
                                )
                            )
                        }
                        else -> {
                            it
                        }
                    }

                }
            )
        )
    }

    fun updateProductsSliderByCategory(position: Int, categoryId: Long) {
        when(position) {
            POSITION_9_TAB -> updateStateByTabAndProductPositions(POSITION_9_TAB, POSITION_10, categoryId)
            POSITION_18_TAB -> updateStateByTabAndProductPositions(POSITION_18_TAB, POSITION_19, categoryId)
        }
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
        val item: Item?,
    )

    data class PositionItemWithTitle(
        val item: PositionItem,
        val itemTitle: PositionItem,
    )

    data class PositionItemWithTabs(
        val item: PositionItem,
        val itemTabs: PositionItem,
    )

    sealed class HomeEvents : Event {
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) :
            HomeEvents()

        object GoToProfile : HomeEvents()
        object SendComment : HomeEvents()
    }

    data class HomeState(
        val items: List<PositionItem>,
        val itemsInt: List<Int>,
        val news: PopupNewsUI? = null,
        val hasShow: Boolean = false,
    ) : State {

        companion object {
            fun idle(): HomeState {
                return HomeState(
                    listOf(
                        PositionItem(POSITION_13, HomeTripleNav(POSITION_13)),
                        PositionItem(POSITION_27, HomeBottomInfo(POSITION_27))
                    ),
                    itemsInt = listOf(POSITION_13, POSITION_13)
                )
            }
        }
    }

    companion object {
        const val POSITION_1 = 1
        const val POSITION_2_TITLE = 2
        const val POSITION_3 = 3
        const val POSITION_4_TITLE = 4
        const val POSITION_5 = 5
        const val POSITION_6_TITLE = 6
        const val POSITION_7 = 7
        const val POSITION_8 = 8
        const val POSITION_9_TAB = 9
        const val POSITION_10 = 10
        const val POSITION_11_TITLE = 11
        const val POSITION_12 = 12
        const val POSITION_13 = 13
        const val POSITION_14_TITLE = 14
        const val POSITION_15 = 15
        const val POSITION_16_TITLE = 16
        const val POSITION_17 = 17
        const val POSITION_18_TAB = 18
        const val POSITION_19 = 19
        const val POSITION_20_TITLE = 20
        const val POSITION_21 = 21
        const val POSITION_22 = 22
        const val POSITION_23_TITLE = 23
        const val POSITION_24 = 24
        const val POSITION_25_TITLE = 25
        const val POSITION_26 = 26
        const val POSITION_27 = 27

        const val ENTER_COUNT = 8
        const val POSITIONS_COUNT = 27
    }
}