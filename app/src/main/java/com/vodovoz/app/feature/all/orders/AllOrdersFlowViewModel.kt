package com.vodovoz.app.feature.all.orders

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.order.AllOrdersResponseJsonParser.parseAllOrdersSliderResponse
import com.vodovoz.app.data.parser.response.order.OrderDetailsResponseJsonParser.parseOrderDetailsResponse
import com.vodovoz.app.mapper.OrderDetailsMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager
): PagingStateViewModel<AllOrdersFlowViewModel.AllOrdersState>(AllOrdersState()) {

    private val goToCartListener = MutableSharedFlow<Boolean>()
    fun observeGoToCart() = goToCartListener.asSharedFlow()

    private val goToFilterListener = MutableSharedFlow<OrdersFiltersBundleUI>()
    fun observeGoToFilter() = goToFilterListener.asSharedFlow()

    fun goToFilter() {
        viewModelScope.launch {
            goToFilterListener.emit(state.data.ordersFiltersBundleUI)
        }
    }

    private fun fetchAllOrders() {
        val userId = dataRepository.fetchUserId() ?: return
        viewModelScope.launch {
            flow { emit(repository.fetchAllOrders(
                userId = userId,
                page = state.page,
                appVersion = BuildConfig.VERSION_NAME,
                orderId = state.data.ordersFiltersBundleUI.orderId,
                status = StringBuilder().apply {
                    state.data.ordersFiltersBundleUI.orderStatusUIList.forEach { append(it.id).append(",") }
                }.toString()
            )) }
                .catch {
                    debugLog { "fetch all orders sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseAllOrdersSliderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = if (data.isEmpty() && !state.loadMore) {
                            state.copy(
                                error = ErrorState.Empty(),
                                loadingPage = false,
                                loadMore = false,
                                bottomItem = null,
                                page = 1
                            )
                        } else {

                            val itemsList = if (state.loadMore) {
                                state.data.itemsList + data
                            } else {
                                data
                            }

                            state.copy(
                                page = if (data.isEmpty()) null else state.page?.plus(1),
                                loadingPage = false,
                                data = state.data.copy(itemsList = itemsList),
                                error = null,
                                loadMore = false,
                                bottomItem = null
                            )
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error(),
                                page = 1,
                                loadMore = false
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun firstLoadSorted() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            fetchAllOrders()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchAllOrders()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem())
            fetchAllOrders()
        }
    }

    fun updateFilterBundle(filterBundle: OrdersFiltersBundleUI) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                ordersFiltersBundleUI = filterBundle
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchAllOrders()
    }

    fun repeatOrder(orderId: Long) {
        val userId = dataRepository.fetchUserId() ?: return
        viewModelScope.launch {
            flow { emit(repository.fetchOrderDetailsResponse(
                userId = userId,
                appVersion = BuildConfig.VERSION_NAME,
                orderId = orderId
            )) }
                .catch {
                    debugLog { "repeat order error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseOrderDetailsResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        data.productUIList.filter { it.leftItems > 0 }.forEachIndexed {index, product ->
                            cartManager.add(
                                id = product.id,
                                oldCount = product.orderQuantity,
                                newCount = product.orderQuantity,
                                withUpdate = index == data.productUIList.lastIndex-1,
                                repeat = true
                            )
                        }
                        uiStateListener.value =  state.copy(loadingPage = true, error = null)
                        delay(3000)
                        uiStateListener.value =  state.copy(loadingPage = false, error = null)
                        goToCartListener.emit(true)
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun isLoginAlready() = dataRepository.isAlreadyLogin()

    data class AllOrdersState(
        val itemsList: List<Item> = emptyList(),
        val ordersFiltersBundleUI: OrdersFiltersBundleUI = OrdersFiltersBundleUI()
    ) : State
}