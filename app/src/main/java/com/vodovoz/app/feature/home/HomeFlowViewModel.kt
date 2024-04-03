package com.vodovoz.app.feature.home

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.home.viewholders.homebanners.HomeBanners
import com.vodovoz.app.feature.home.viewholders.homebottominfo.HomeBottomInfo
import com.vodovoz.app.feature.home.viewholders.homebrands.HomeBrands
import com.vodovoz.app.feature.home.viewholders.homecomments.HomeComments
import com.vodovoz.app.feature.home.viewholders.homecountries.HomeCountries
import com.vodovoz.app.feature.home.viewholders.homehistories.HomeHistories
import com.vodovoz.app.feature.home.viewholders.homeorders.HomeOrders
import com.vodovoz.app.feature.home.viewholders.homepopulars.HomePopulars
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeProductsTabs
import com.vodovoz.app.feature.home.viewholders.homepromotions.HomePromotions
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.feature.home.viewholders.hometriplenav.HomeTripleNav
import com.vodovoz.app.mapper.BannerMapper.mapToUI
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.CommentMapper.mapToUI
import com.vodovoz.app.mapper.CountriesSliderBundleMapper.mapToUI
import com.vodovoz.app.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.mapper.PopupNewsMapper.mapToUI
import com.vodovoz.app.mapper.PromotionMapper.mapToUI
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager,
) : PagingContractViewModel<HomeFlowViewModel.HomeState, HomeFlowViewModel.HomeEvents>(HomeState.idle()) {

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(loadingPage = true)

            viewModelScope.launch(Dispatchers.IO) {
                updatePopupNews()
                val tasks = firstLoadTasks()
                val start = System.currentTimeMillis()
                val result = awaitAll(*tasks).flatten()
                debugLog { "first load task ${System.currentTimeMillis() - start} result size ${result.size}" }
                val positionItemsSorted =
                    (state.data.positionItems + result).sortedBy { it.position }
                uiStateListener.value = state.copy(
                    loadingPage = false,
                    data = state.data.copy(
                        positionItems = positionItemsSorted,
                        items = positionItemsSorted.map { it.item }),
                    isFirstLoad = true,
                    error = if (result.isNotEmpty()) {
                        null
                    } else {
                        state.error
                    }
                )
            }


        }
    }

    fun secondLoad() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = accountManager.fetchAccountId()
            val tasks = secondLoadTasks(userId)
            val start = System.currentTimeMillis()
            val result = awaitAll(*tasks).flatten()
            val mappedResult = if (result.isNotEmpty()) {
                result + HomeState.fetchStaticItems()
            } else {
                result
            }
            debugLog { "second load task ${System.currentTimeMillis() - start} result size ${mappedResult.size}" }
            val positionItemsSorted =
                (state.data.positionItems + mappedResult).sortedBy { it.position }
            uiStateListener.value = state.copy(
                loadingPage = false,
                data = state.data.copy(
                    positionItems = positionItemsSorted,
                    items = positionItemsSorted.map { it.item },
                    isSecondLoad = true
                ),
                error = if (mappedResult.isNotEmpty()) {
                    null
                } else {
                    state.error
                }
            )
        }
    }

    fun refresh() {
        if (!state.loadingPage) {
            uiStateListener.value =
                state.copy(
                    loadingPage = true,
                    data = state.data.copy(
                        items = HomeState.idle().items,
                        positionItems = HomeState.idle().positionItems,
                        isSecondLoad = false
                    ),
                    isFirstLoad = false
                )
            viewModelScope.launch {
                val userId = accountManager.fetchAccountId()
                val tasks = firstLoadTasks() + secondLoadTasks(userId)
                val start = System.currentTimeMillis()
                val result = awaitAll(*tasks).flatten()
                debugLog { "refresh load task ${System.currentTimeMillis() - start} result size ${result.size}" }
                val mappedResult = if (result.isNotEmpty()) {
                    result + HomeState.fetchStaticItems()
                } else {
                    result
                }
                val positionItemsSorted =
                    (state.data.positionItems + mappedResult).sortedBy { it.position }
                uiStateListener.value = state.copy(
                    loadingPage = false,
                    data = state.data.copy(
                        positionItems = positionItemsSorted,
                        items = positionItemsSorted.map { it.item },
                        isSecondLoad = true
                    ),
                    error = if (mappedResult.isNotEmpty()) {
                        null
                    } else {
                        state.error
                    },
                    isFirstLoad = true
                )
            }
        }
    }

    private fun CoroutineScope.firstLoadTasks() = arrayOf(
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchAdvertisingBannersSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseAdvertisingBannersSliderResponse()
                    if (response is ResponseEntity.Success) {
                        listOf(
                            PositionItem(
                                POSITION_1,
                                HomeBanners(POSITION_1, response.data.mapToUI(), bannerRatio = 0.41)
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response =
                    repository.fetchHistoriesSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseHistoriesSliderResponse()
                    if (response is ResponseEntity.Success) {
                        listOf(
                            PositionItem(
                                POSITION_3,
                                HomeHistories(POSITION_3, response.data.mapToUI())
                            ),
                            PositionItem(
                                POSITION_2_TITLE,
                                HomeTitle(
                                    id = POSITION_2_TITLE,
                                    type = HomeTitle.HISTORIES_TITLE,
                                    name = "Истории"
                                )
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchPopularSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parsePopularSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val mappedData = response.data.mapToUI()
                        listOf(
                            PositionItem(
                                POSITION_5,
                                HomePopulars(POSITION_5, mappedData.categoryList)
                            ),
                            PositionItem(
                                POSITION_4_TITLE,
                                HomeTitle(
                                    id = POSITION_4_TITLE,
                                    type = HomeTitle.POPULARS_TITLE,
                                    name = "Популярные разделы",
                                    categoryProductsName = mappedData.name,
                                )
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchDiscountsSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseDiscountSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        listOf(
                            PositionItem(
                                POSITION_7,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.DISCOUNT,
                                    POSITION_7
                                )
                            ),
                            PositionItem(
                                POSITION_6_TITLE,
                                HomeTitle(
                                    id = POSITION_6_TITLE,
                                    type = HomeTitle.DISCOUNT_TITLE,
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
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchCategoryBannersSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseCategoryBannersSliderResponse()
                    if (response is ResponseEntity.Success) {
                        listOf(
                            PositionItem(
                                POSITION_8,
                                HomeBanners(POSITION_8, response.data.mapToUI(), bannerRatio = 0.5)
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        }
    )

//    fun fetchHomeProductsByType(
//        data: List<CategoryDetailUI>,
//        type: Int,
//        position: Int,
//    ): HomeProducts {
//        return HomeProducts(
//            position,
//            data,
//            productsType = type,
//            productsSliderConfig = ProductsSliderConfig(
//                containShowAllButton = true
//            ),
//            prodList = data.first().productUIList
//        )
//    }

    private fun CoroutineScope.secondLoadTasks(userId: Long?) = arrayOf(
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchTopSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseTopSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        listOf(
                            PositionItem(
                                POSITION_10,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.TOP_PROD,
                                    POSITION_10
                                )
                            ),
                            PositionItem(
                                POSITION_9_TAB,
                                HomeProductsTabs(
                                    id = POSITION_9_TAB,
                                    data.mapIndexed { index, cat ->
                                        if (index == 0) {
                                            cat.copy(isSelected = true, position = POSITION_9_TAB)
                                        } else {
                                            cat.copy(isSelected = false, position = POSITION_9_TAB)
                                        }
                                    })
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                if (userId == null) {
                    return@coroutineScope emptyList()
                }
                val response = repository.fetchOrdersSliderProfile(userId)
                withContext(Dispatchers.Default) {
                    if (response is ResponseEntity.Success) {
                        listOf(
                            PositionItem(
                                POSITION_12,
                                HomeOrders(POSITION_12, response.data.mapToUI())
                            ),
                            PositionItem(
                                POSITION_11_TITLE,
                                HomeTitle(
                                    id = POSITION_11_TITLE,
                                    type = HomeTitle.ORDERS_TITLE,
                                    name = "Мои заказы",
                                    showAll = true,
                                    showAllName = "СМ.ВСЕ"
                                )
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchNoveltiesSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseNoveltiesSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        listOf(
                            PositionItem(
                                POSITION_15,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.NOVELTIES,
                                    POSITION_15
                                )
                            ),
                            PositionItem(
                                POSITION_14_TITLE,
                                HomeTitle(
                                    id = POSITION_14_TITLE,
                                    type = HomeTitle.NOVELTIES_TITLE,
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
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchPromotionsSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parsePromotionSliderResponse()
                    if (response is ResponseEntity.Success) {
                        listOf(
                            PositionItem(
                                POSITION_17, HomePromotions(
                                    POSITION_17, PromotionsSliderBundleUI(
                                        title = "Акции",
                                        containShowAllButton = true,
                                        promotionUIList = response.data.mapToUI()
                                    )
                                )
                            ),
                            PositionItem(
                                POSITION_16_TITLE,
                                HomeTitle(
                                    id = POSITION_16_TITLE,
                                    type = HomeTitle.PROMOTIONS_TITLE,
                                    name = "Акции",
                                    showAll = true,
                                    showAllName = "СМ.ВСЕ",
                                    lightBg = false
                                )
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchBottomSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseBottomSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        listOf(
                            PositionItem(
                                POSITION_19,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.BOTTOM_PROD,
                                    POSITION_19
                                )
                            ),
                            PositionItem(
                                POSITION_18_TAB,
                                HomeProductsTabs(
                                    id = POSITION_18_TAB,
                                    data.mapIndexed { index, cat ->
                                        if (index == 0) {
                                            cat.copy(isSelected = true, position = POSITION_18_TAB)
                                        } else {
                                            cat.copy(isSelected = false, position = POSITION_18_TAB)
                                        }
                                    })
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchBrandsSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseBrandsSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val mappedData = response.data.mapToUI()
                        listOf(
                            PositionItem(
                                POSITION_21,
                                HomeBrands(POSITION_21, mappedData.brandsList)
                            ),
                            PositionItem(
                                POSITION_20_TITLE,
                                HomeTitle(
                                    id = POSITION_20_TITLE,
                                    type = HomeTitle.BRANDS_TITLE,
                                    name = "Бренды",
                                    showAll = true,
                                    showAllName = "СМ.ВСЕ",
                                    categoryProductsName = mappedData.name
                                )
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchCountriesSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseCountriesSliderResponse()
                    if (response is ResponseEntity.Success) {
                        listOf(
                            PositionItem(
                                POSITION_22,
                                HomeCountries(POSITION_22, response.data.mapToUI())
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                if (userId == null) {
                    return@coroutineScope emptyList()
                }

                val response = repository.fetchViewedProductsSlider(userId)
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseViewedProductsSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        listOf(
                            PositionItem(
                                POSITION_24,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.VIEWED,
                                    POSITION_24
                                )
                            ),
                            PositionItem(
                                POSITION_23_TITLE,
                                HomeTitle(
                                    id = POSITION_23_TITLE,
                                    type = HomeTitle.VIEWED_TITLE,
                                    name = "Вы смотрели",
                                    showAll = false,
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
                        emptyList()
                    }
                }
            }
        },
        homeRequestAsync {
            coroutineScope {
                val response = repository.fetchCommentsSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseCommentsSliderResponse()
                    if (response is ResponseEntity.Success) {
                        listOf(
                            PositionItem(
                                POSITION_26,
                                HomeComments(POSITION_26, response.data.mapToUI())
                            ),
                            PositionItem(
                                POSITION_25_TITLE,
                                HomeTitle(
                                    id = POSITION_25_TITLE,
                                    type = HomeTitle.COMMENTS_TITLE,
                                    name = "Отзывы",
                                    showAll = true,
                                    showAllName = "Написать отзыв"
                                )
                            )
                        )
                    } else {
                        emptyList()
                    }
                }
            }
        }
    )

    private inline fun CoroutineScope.homeRequestAsync(crossinline request: suspend () -> List<PositionItem>): Deferred<List<PositionItem>> {
        return async(Dispatchers.IO) {
            runCatching { request.invoke() }
                .onFailure { showNetworkError(it) }
                .getOrDefault(emptyList())
        }
    }

    private fun showNetworkError(throwable: Throwable) {
        val error = throwable.toErrorState()
        if (error is ErrorState.NetworkError) {
            uiStateListener.value = state.copy(error = error)
        }
    }

    private fun updatePopupNews() {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId()
            flow { emit(repository.fetchPopupNews(userId)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                news = response.data.mapToUI()
                            )
                        )
                    }
                }
                .catch { debugLog { "fetch popup news error ${it.localizedMessage}" } }
                .collect()
        }
    }

    private fun updateStateByTabAndProductPositions(
        positionTab: Int,
        position: Int,
        categoryId: Long,
    ) {
        val positionItems = state.data.positionItems.map {
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
                            tabsNames = it.item.tabsNames.map { cat ->
                                cat.copy(isSelected = cat.id == categoryId)
                            }
                        )
                    )
                }
                else -> {
                    it
                }
            }
        }

        uiStateListener.value = state.copy(
            data = state.data.copy(
                items = positionItems.map { it.item },
                positionItems = positionItems
            )
        )
    }

    fun updateProductsSliderByCategory(position: Int, categoryId: Long) {
        when (position) {
            POSITION_9_TAB -> updateStateByTabAndProductPositions(
                POSITION_9_TAB,
                POSITION_10,
                categoryId
            )
            POSITION_18_TAB -> updateStateByTabAndProductPositions(
                POSITION_18_TAB,
                POSITION_19,
                categoryId
            )
        }
    }

    fun onPreOrderClick(id: Long, name: String, detailPicture: String) {
        viewModelScope.launch {
            eventListener.emit(HomeEvents.GoToPreOrder(id, name, detailPicture))
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

    fun goToProfile() {
        viewModelScope.launch {
            eventListener.emit(HomeEvents.GoToProfile)
        }
    }

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

    fun repeatOrder(orderId: Long) {
        val userId =
            accountManager.fetchAccountId() ?: return
        uiStateListener.value = state.copy(loadingPage = true, error = null)
        viewModelScope.launch {
            flow {
                emit(
                    repository.repeatOrder(
                        userId = userId,
                        orderId = orderId
                    )
                )
            }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        cartManager.updateCartListState(true)
                        uiStateListener.value = state.copy(loadingPage = false, error = null)
                        eventListener.emit(HomeEvents.GoToCart)
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "repeat order error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    data class PositionItem(
        val position: Int,
        val item: Item,
    )

    sealed class HomeEvents : Event {
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) :
            HomeEvents()

        object GoToProfile : HomeEvents()
        object SendComment : HomeEvents()
        object GoToCart : HomeEvents()
    }

    data class HomeState(
        val positionItems: List<PositionItem>,
        val items: List<Item>,
        val news: PopupNewsUI? = null,
        val hasShow: Boolean = false,
        val isSecondLoad: Boolean = false,
    ) : State {

        companion object {
            fun idle(): HomeState {
                return HomeState(
                    positionItems = emptyList(),
                    items = emptyList()
                )
            }

            fun fetchStaticItems(): List<PositionItem> {
                return listOf(
                    PositionItem(
                        POSITION_13,
                        HomeTripleNav(POSITION_13)
                    ),
                    PositionItem(
                        POSITION_27,
                        HomeBottomInfo(POSITION_27)
                    )
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
    }
}