package com.vodovoz.app.feature.all.orders

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.OrderListMapper.mapToUI
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
//    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
) : PagingContractViewModel<AllOrdersFlowViewModel.AllOrdersState, AllOrdersFlowViewModel.AllOrdersEvent>(
    AllOrdersState()
) {

    fun goToFilter() {
        viewModelScope.launch {
            eventListener.emit(AllOrdersEvent.GoToFilter(state.data.ordersFiltersBundleUI))
        }
    }

    private fun fetchAllOrders() {
        val userId =
            accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchAllOrders(
                        userId = userId,
                        page = state.page,
                        appVersion = BuildConfig.VERSION_NAME,
                        orderId = state.data.ordersFiltersBundleUI.orderId,
                        status = StringBuilder().apply {
                            state.data.ordersFiltersBundleUI.orderFilterUIList.forEach {
                                if(it.isChecked) {
                                    append(it.id).append(
                                        ","
                                    )
                                }
                            }
                        }.toString()
                    )
                )
            }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = if (data.orders.isEmpty() && !state.loadMore) {
                            state.copy(
                                error = ErrorState.Empty(),
                                loadingPage = false,
                                loadMore = false,
                                bottomItem = null,
                                page = 1,
                                data = state.data.copy(
                                    errorTitle = data.title,
                                    errorMessage = data.message,
                                    itemsList = listOf()
                                )
                            )
                        } else {

                            val itemsList = if (state.loadMore) {
                                state.data.itemsList + data.orders
                            } else {
                                data.orders
                            }

                            state.copy(
                                page = if (data.orders.isEmpty()) null else state.page?.plus(1),
                                loadingPage = false,
                                data = state.data.copy(
                                    itemsList = itemsList,
                                    ordersFiltersBundleUI = if (state.data.ordersFiltersBundleUI.orderFilterUIList.isEmpty()) {
                                        OrdersFiltersBundleUI().apply {
                                            orderFilterUIList.addAll(data.filters)
                                        }
                                    } else {
                                        state.data.ordersFiltersBundleUI
                                    }
                                ),
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
                .catch {
                    debugLog { "fetch all orders sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
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
        var filterCount = if(filterBundle.orderId != null) 1 else 0
        filterBundle.orderFilterUIList.forEach {
            if (it.isChecked) {
                filterCount++
            }
        }
        uiStateListener.value = state.copy(
            data = state.data.copy(
                ordersFiltersBundleUI = filterBundle,
                filterCount = filterCount
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchAllOrders()
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
                        eventListener.emit(AllOrdersEvent.GoToCart)
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

    fun isLoginAlready() = accountManager.isAlreadyLogin()

    data class AllOrdersState(
        val itemsList: List<Item> = emptyList(),
        val ordersFiltersBundleUI: OrdersFiltersBundleUI = OrdersFiltersBundleUI(),
        val filterCount: Int = 0,
        val errorTitle: String = "",
        val errorMessage: String = "",
    ) : State

    sealed class AllOrdersEvent : Event {
        object GoToCart : AllOrdersEvent()
        data class GoToFilter(val bundle: OrdersFiltersBundleUI) : AllOrdersEvent()
    }
}