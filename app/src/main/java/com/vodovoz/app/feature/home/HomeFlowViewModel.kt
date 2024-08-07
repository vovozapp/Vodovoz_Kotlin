package com.vodovoz.app.feature.home

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.toErrorState
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
import com.vodovoz.app.feature.home.viewholders.homesections.HomeSections
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
import com.vodovoz.app.mapper.TopSectionsMapper.mapToUI
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.ui.model.custom.PromotionsSliderBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    (state.data.positionItems + result).toSet().sortedBy { it.position }
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
                secondLoad()
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
                (state.data.positionItems + mappedResult).toSet().sortedBy { it.position }
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
                    (state.data.positionItems + mappedResult).toSet().sortedBy { it.position }
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
                                POSITION_10,
                                HomeBanners(
                                    POSITION_10,
                                    response.data.mapToUI(),
                                    bannerRatio = 0.41
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
                val response = repository.fetchTopSlider()
                withContext(Dispatchers.Default) {
//                    val response = responseBody.parseTopSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val retList = mutableListOf<PositionItem>()
                        retList.addAll(
                            if (response.data.categoryDetailsList != null) {
                                val data = response.data.categoryDetailsList.mapToUI()
                                listOf(
                                    PositionItem(
                                        POSITION_100,
                                        HomeProducts.fetchHomeProductsByType(
                                            data,
                                            HomeProducts.TOP_PROD,
                                            POSITION_100
                                        )
                                    ),
                                    if (data.size != 1) {
                                        PositionItem(
                                            POSITION_90_TAB,
                                            HomeProductsTabs(
                                                id = POSITION_90_TAB,
                                                data.mapIndexed { index, cat ->
                                                    if (index == 0) {
                                                        cat.copy(
                                                            isSelected = true,
                                                            position = POSITION_90_TAB
                                                        )
                                                    } else {
                                                        cat.copy(
                                                            isSelected = false,
                                                            position = POSITION_90_TAB
                                                        )
                                                    }
                                                })
                                        )
                                    } else {
                                        PositionItem(
                                            POSITION_90_TAB,
                                            HomeTitle(
                                                id = POSITION_90_TAB,
                                                type = HomeTitle.SLIDER_TITLE,
                                                name = "",
                                                showAll = true,
                                                showAllName = "СМ.ВСЕ",
                                                categoryProductsName = data.first().name,
                                                titleId = data.first().id
                                            )
                                        )
                                    }
                                )
                            } else {
                                emptyList()
                            }
                        )
                        retList.addAll(
                            if (response.data.sectionsEntity != null) {
                                val data = response.data.sectionsEntity.mapToUI()

                                listOf(
                                    PositionItem(
                                        POSITION_15,
                                        HomeSections(
                                            id = POSITION_15,
                                            items = data,
                                        )
                                    )
                                )
                            } else {
                                emptyList()
                            }
                        )
                        retList
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
                                POSITION_30,
                                HomeHistories(POSITION_30, response.data.mapToUI())
                            ),
                            PositionItem(
                                POSITION_20_TITLE,
                                HomeTitle(
                                    id = POSITION_20_TITLE,
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
                                POSITION_50,
                                HomePopulars(POSITION_50, mappedData.categoryList)
                            ),
                            PositionItem(
                                POSITION_40_TITLE,
                                HomeTitle(
                                    id = POSITION_40_TITLE,
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
                                POSITION_70,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.DISCOUNT,
                                    POSITION_70
                                )
                            ),
                            PositionItem(
                                POSITION_60_TITLE,
                                HomeTitle(
                                    id = POSITION_60_TITLE,
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
                                POSITION_80,
                                HomeBanners(POSITION_80, response.data.mapToUI(), bannerRatio = 0.5)
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
                if (userId == null) {
                    return@coroutineScope emptyList()
                }
                val response = repository.fetchOrdersSliderProfile(userId)
                withContext(Dispatchers.Default) {
                    if (response is ResponseEntity.Success) {
                        listOf(
                            PositionItem(
                                POSITION_120,
                                HomeOrders(POSITION_120, response.data.mapToUI())
                            ),
                            PositionItem(
                                POSITION_110_TITLE,
                                HomeTitle(
                                    id = POSITION_110_TITLE,
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
                                POSITION_150,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.NOVELTIES,
                                    POSITION_150
                                )
                            ),
                            PositionItem(
                                POSITION_140_TITLE,
                                HomeTitle(
                                    id = POSITION_140_TITLE,
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
                                POSITION_170, HomePromotions(
                                    POSITION_170, PromotionsSliderBundleUI(
                                        title = "Акции",
                                        containShowAllButton = true,
                                        promotionUIList = response.data.mapToUI()
                                    )
                                )
                            ),
                            PositionItem(
                                POSITION_160_TITLE,
                                HomeTitle(
                                    id = POSITION_160_TITLE,
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
                                POSITION_190,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.BOTTOM_PROD,
                                    POSITION_190
                                )
                            ),
                            if (data.size != 1) {
                                PositionItem(
                                    POSITION_180_TAB,
                                    HomeProductsTabs(
                                        id = POSITION_180_TAB,
                                        data.mapIndexed { index, cat ->
                                            if (index == 0) {
                                                cat.copy(
                                                    isSelected = true,
                                                    position = POSITION_180_TAB
                                                )
                                            } else {
                                                cat.copy(
                                                    isSelected = false,
                                                    position = POSITION_180_TAB
                                                )
                                            }
                                        })
                                )
                            } else {
                                PositionItem(
                                    POSITION_180_TAB,
                                    HomeTitle(
                                        id = POSITION_180_TAB,
                                        type = HomeTitle.SLIDER_TITLE,
                                        name = "",
                                        showAll = true,
                                        showAllName = "СМ.ВСЕ",
                                        categoryProductsName = data.first().name,
                                        titleId = data.first().id
                                    )
                                )
                            }
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
                                POSITION_210,
                                HomeBrands(POSITION_210, mappedData.brandsList)
                            ),
                            PositionItem(
                                POSITION_200_TITLE,
                                HomeTitle(
                                    id = POSITION_200_TITLE,
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
                                POSITION_220,
                                HomeCountries(POSITION_220, response.data.mapToUI())
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
                        val data = listOf(response.data.mapToUI())
                        listOf(
                            PositionItem(
                                POSITION_240,
                                HomeProducts.fetchHomeProductsByType(
                                    data,
                                    HomeProducts.VIEWED,
                                    POSITION_240
                                )
                            ),
                            PositionItem(
                                POSITION_230_TITLE,
                                HomeTitle(
                                    id = POSITION_230_TITLE,
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
                                POSITION_260,
                                HomeComments(POSITION_260, response.data.mapToUI())
                            ),
                            PositionItem(
                                POSITION_250_TITLE,
                                HomeTitle(
                                    id = POSITION_250_TITLE,
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
            POSITION_90_TAB -> updateStateByTabAndProductPositions(
                POSITION_90_TAB,
                POSITION_100,
                categoryId
            )

            POSITION_180_TAB -> updateStateByTabAndProductPositions(
                POSITION_180_TAB,
                POSITION_190,
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

    fun onSectionsTabClick(title: String) {

        val positionItems = state.data.positionItems.map { positionItem ->
            when (positionItem.position) {
                POSITION_15 -> {
                    positionItem.copy(
                        item = (positionItem.item as HomeSections).copy(
                            items = positionItem.item.items.copy(
                                parentSectionDataUIList = positionItem.item.items.parentSectionDataUIList.map {
                                    it.copy(
                                        isSelected = it.title == title
                                    )
                                }
                            )
                        )
                    )
                }

                else -> {
                    positionItem
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
                        POSITION_130,
                        HomeTripleNav(POSITION_130)
                    ),
                    PositionItem(
                        POSITION_270,
                        HomeBottomInfo(POSITION_270)
                    )
                )
            }
        }
    }

    companion object {
        const val POSITION_10 = 10
        const val POSITION_15 = 15
        const val POSITION_20_TITLE = 20
        const val POSITION_30 = 30
        const val POSITION_40_TITLE = 40
        const val POSITION_50 = 50
        const val POSITION_60_TITLE = 60
        const val POSITION_70 = 70
        const val POSITION_80 = 80
        const val POSITION_90_TAB = 90
        const val POSITION_100 = 100
        const val POSITION_110_TITLE = 110
        const val POSITION_120 = 120
        const val POSITION_130 = 130
        const val POSITION_140_TITLE = 140
        const val POSITION_150 = 150
        const val POSITION_160_TITLE = 160
        const val POSITION_170 = 170
        const val POSITION_180_TAB = 180
        const val POSITION_190 = 190
        const val POSITION_200_TITLE = 200
        const val POSITION_210 = 210
        const val POSITION_220 = 220
        const val POSITION_230_TITLE = 230
        const val POSITION_240 = 240
        const val POSITION_250_TITLE = 250
        const val POSITION_260 = 260
        const val POSITION_270 = 270
    }
}