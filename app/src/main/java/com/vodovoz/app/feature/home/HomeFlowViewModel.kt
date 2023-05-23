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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
                debugLog { "first load task ${System.currentTimeMillis() - start} result size $result" }
                uiStateListener.value = state.copy(
                    loadingPage = false,
                    data = state.data.copy(items = state.data.items + result),
                    isFirstLoad = true,
                    error = null
                )
            }
        }
    }

    fun secondLoad() {
        viewModelScope.launch {
            val tasks = secondLoadTasks()
            val start = System.currentTimeMillis()
            val result = awaitAll(*tasks).flatten() + HomeState.fetchStaticItems()
            debugLog { "second load task ${System.currentTimeMillis() - start} result size $result" }
            uiStateListener.value = state.copy(
                loadingPage = false,
                data = state.data.copy(items = state.data.items + result, isSecondLoad = true),
                error = null
            )
        }
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(
                loadingPage = true,
                data = state.data.copy(
                    items = HomeState.idle().items
                )
            )
        viewModelScope.launch {
            val tasks = firstLoadTasks() + secondLoadTasks()
            val result = awaitAll(*tasks).flatten()
            val mappedResult = if (result.isNotEmpty()) {
                result + HomeState.fetchStaticItems()
            } else {
                result
            }
            uiStateListener.value = state.copy(
                loadingPage = false,
                data = state.data.copy(items = state.data.items + mappedResult),
                error = null
            )
        }
    }

    private fun CoroutineScope.firstLoadTasks() = arrayOf(
        async(Dispatchers.IO) { fetchAdvBannerSlider(POSITION_1) },
        async(Dispatchers.IO) { fetchHisSlider(POSITION_2_TITLE, POSITION_3) },
        async(Dispatchers.IO) { fetchPopSlider(POSITION_4_TITLE, POSITION_5) },
        async(Dispatchers.IO) { fetchDiscSlider(POSITION_6_TITLE, POSITION_7) },
        async(Dispatchers.IO) { fetchCatBannerSlider(POSITION_8) }
    )

    private fun CoroutineScope.secondLoadTasks() = arrayOf(
        async(Dispatchers.IO) { fetchTSlider(POSITION_9_TAB, POSITION_10) },
        async(Dispatchers.IO) { fetchOrdSlider(POSITION_11_TITLE, POSITION_12) },
        async(Dispatchers.IO) { fetchNovSlider(POSITION_14_TITLE, POSITION_15) },
        async(Dispatchers.IO) { fetchPromSlider(POSITION_16_TITLE, POSITION_17) },
        async(Dispatchers.IO) { fetchBSlider(POSITION_18_TAB, POSITION_19) },
        async(Dispatchers.IO) { fetchBrSlider(POSITION_20_TITLE, POSITION_21) },
        async(Dispatchers.IO) { fetchCountrSlider(POSITION_22) },
        async(Dispatchers.IO) { fetchViSlider(POSITION_23_TITLE, POSITION_24) },
        async(Dispatchers.IO) { fetchCommSlider(POSITION_25_TITLE, POSITION_26) }
    )

    private suspend fun fetchAdvBannerSlider(position: Int): List<PositionItem> {
        return runCatching { repository.fetchAdvertisingBannersSlider(position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchHisSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchHistoriesSlider(positionTitle, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchPopSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchPopularSlider(positionTitle, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchDiscSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchDiscountsSlider(positionTitle, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchCatBannerSlider(position: Int): List<PositionItem> {
        return runCatching { repository.fetchCategoryBannersSlider(position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchTSlider(positionTab: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchTopSlider(positionTab, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchOrdSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching {
            val userId = accountManager.fetchAccountId()
            repository.fetchOrdersSlider(userId, positionTitle, position)
        }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchNovSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchNoveltiesSlider(positionTitle, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchPromSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchPromotionsSlider(positionTitle, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchBSlider(positionTab: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchBottomSlider(positionTab, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }


    private suspend fun fetchBrSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchBrandsSlider(positionTitle, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchCountrSlider(position: Int): List<PositionItem> {
        return runCatching { repository.fetchCountriesSlider(position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchViSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching {
            val userId = accountManager.fetchAccountId()
            repository.fetchViewedProductsSlider(userId, positionTitle, position)
        }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private suspend fun fetchCommSlider(positionTitle: Int, position: Int): List<PositionItem> {
        return runCatching { repository.fetchCommentsSlider(positionTitle, position) }
            .onFailure { showNetworkError(it) }
            .getOrDefault(emptyList())
    }

    private fun showNetworkError(throwable: Throwable) {
        val error = throwable.toErrorState()
        if (error is ErrorState.NetworkError) {
            uiStateListener.value = state.copy(error = error)
        }
    }

    private fun updatePopupNews() {
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId() ?: return@launch
            flow { emit(repository.fetchPopupNews(userId)) }
                .flowOn(Dispatchers.IO)
                .catch { debugLog { "fetch popup news error ${it.localizedMessage}" } }
                .collect {
                    if (it != null) {
                        uiStateListener.value = state.copy(data = state.data.copy(news = it))
                    }
                }
        }
    }

    private fun updateStateByTabAndProductPositions(
        positionTab: Int,
        position: Int,
        categoryId: Long,
    ) {
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
                                    tabsNames = it.item.tabsNames.map { cat ->
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

    fun isLoginAlready() = accountManager.isAlreadyLogin()

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
        val items: List<PositionItem>,
        val news: PopupNewsUI? = null,
        val hasShow: Boolean = false,
        val isSecondLoad: Boolean = false
    ) : State {

        companion object {
            fun idle(): HomeState {
                return HomeState(
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

        const val POSITIONS_COUNT = 27
    }
}