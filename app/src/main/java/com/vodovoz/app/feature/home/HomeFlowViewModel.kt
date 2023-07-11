package com.vodovoz.app.feature.home

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.home.viewholders.homebottominfo.HomeBottomInfo
import com.vodovoz.app.feature.home.viewholders.homeproducts.HomeProducts
import com.vodovoz.app.feature.home.viewholders.homeproductstabs.HomeProductsTabs
import com.vodovoz.app.feature.home.viewholders.hometriplenav.HomeTripleNav
import com.vodovoz.app.ui.model.PopupNewsUI
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

            viewModelScope.launch {
                updatePopupNews()
                val tasks = firstLoadTasks()
                val start = System.currentTimeMillis()
                val result = awaitAll(*tasks).flatten()
                debugLog { "first load task ${System.currentTimeMillis() - start} result size ${result.size}" }
                val positionItemsSorted = (state.data.positionItems + result).sortedBy { it.position }
                uiStateListener.value = state.copy(
                    loadingPage = false,
                    data = state.data.copy(positionItems = positionItemsSorted, items = positionItemsSorted.map { it.item }),
                    isFirstLoad = true,
                    error = if (result.isNotEmpty()) {
                        null
                    } else {
                        state.error
                    }
                )
            }

            secondLoad()
        }
    }

    private fun secondLoad() {
        viewModelScope.launch {
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
            val positionItemsSorted = (state.data.positionItems + mappedResult).sortedBy { it.position }
            uiStateListener.value = state.copy(
                loadingPage = false,
                data = state.data.copy(positionItems = positionItemsSorted, items = positionItemsSorted.map { it.item }, isSecondLoad = true),
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
                val result = awaitAll(*tasks).flatten()
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
        homeRequestAsync { repository.fetchAdvertisingBannersSlider(POSITION_1) },
        homeRequestAsync { repository.fetchHistoriesSlider(POSITION_2_TITLE, POSITION_3) },
        homeRequestAsync { repository.fetchPopularSlider(POSITION_4_TITLE, POSITION_5) },
        homeRequestAsync { repository.fetchDiscountsSlider(POSITION_6_TITLE, POSITION_7) },
        homeRequestAsync { repository.fetchCategoryBannersSlider(POSITION_8) }
    )

    private fun CoroutineScope.secondLoadTasks(userId: Long?) = arrayOf(
        homeRequestAsync { repository.fetchTopSlider(POSITION_9_TAB, POSITION_10) },
        homeRequestAsync { repository.fetchOrdersSlider(userId, POSITION_11_TITLE, POSITION_12) },
        homeRequestAsync { repository.fetchNoveltiesSlider(POSITION_14_TITLE, POSITION_15) },
        homeRequestAsync { repository.fetchPromotionsSlider(POSITION_16_TITLE, POSITION_17) },
        homeRequestAsync { repository.fetchBottomSlider(POSITION_18_TAB, POSITION_19) },
        homeRequestAsync { repository.fetchBrandsSlider(POSITION_20_TITLE, POSITION_21) },
        homeRequestAsync { repository.fetchCountriesSlider(POSITION_22) },
        homeRequestAsync { repository.fetchViewedProductsSlider(userId, POSITION_23_TITLE, POSITION_24) },
        homeRequestAsync { repository.fetchCommentsSlider(POSITION_25_TITLE, POSITION_26) }
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
                .filterNotNull()
                .onEach { uiStateListener.value = state.copy(data = state.data.copy(news = it)) }
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
        val item: Item,
    )

    sealed class HomeEvents : Event {
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) :
            HomeEvents()

        object GoToProfile : HomeEvents()
        object SendComment : HomeEvents()
    }

    data class HomeState(
        val positionItems: List<PositionItem>,
        val items: List<Item>,
        val news: PopupNewsUI? = null,
        val hasShow: Boolean = false,
        val isSecondLoad: Boolean = false
    ) : State {

        companion object {
            fun idle(): HomeState {
                return HomeState(
                    positionItems = emptyList(),
                    items = emptyList()
                )
            }

            fun fetchStaticItems() : List<PositionItem> {
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