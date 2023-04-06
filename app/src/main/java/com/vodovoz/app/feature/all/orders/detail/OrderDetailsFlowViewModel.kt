package com.vodovoz.app.feature.all.orders.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.order.CancelOrderResponseJsonParser.parseCancelOrderResponse
import com.vodovoz.app.data.parser.response.order.OrderDetailsResponseJsonParser.parseOrderDetailsResponse
import com.vodovoz.app.mapper.OrderDetailsMapper.mapToUI
import com.vodovoz.app.ui.model.OrderDetailsUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : PagingStateViewModel<OrderDetailsFlowViewModel.OrderDetailsState>(OrderDetailsState()) {

    private val orderId = savedState.get<Long>("orderId")

    private val goToCartListener = MutableSharedFlow<Boolean>()
    fun observeGoToCart() = goToCartListener.asSharedFlow()

    private val cancelResultListener = MutableSharedFlow<String>()
    fun observeCancelResult() = cancelResultListener.asSharedFlow()

    private fun fetchOrderDetails() {
        val userId = dataRepository.fetchUserId() ?: return
        val id = orderId ?: return
        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchOrderDetailsResponse(
                        userId = userId,
                        appVersion = BuildConfig.VERSION_NAME,
                        orderId = id
                    )
                )
            }
                .catch {
                    debugLog { "fetch order details error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseOrderDetailsResponse()
                    if (response is ResponseEntity.Success) {
                        val orderDetails = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(orderDetailsUI = orderDetails),
                            error = null
                        )
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

    fun repeatOrder() {
        val userId = dataRepository.fetchUserId() ?: return
        val id = orderId ?: return
        viewModelScope.launch {
            flow { emit(repository.fetchOrderDetailsResponse(
                userId = userId,
                appVersion = BuildConfig.VERSION_NAME,
                orderId = id
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

                        data.productUIList.filter { it.isAvailable }.forEachIndexed {index, product ->
                            cartManager.add(
                                id = product.id,
                                oldCount = product.orderQuantity,
                                newCount = product.orderQuantity,
                                withUpdate = index == data.productUIList.lastIndex-1,
                                repeat = true
                            )
                        }
                        uiStateListener.value = state.copy(loadingPage = true, error = null)
                        delay(3000)
                        uiStateListener.value = state.copy(loadingPage = false, error = null)
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

    fun cancelOrder() {
        val id = orderId ?: return
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.cancelOrder(id)) }
                .catch {
                    debugLog { "repeat order error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCancelOrderResponse()
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(loadingPage = false)
                        cancelResultListener.emit(response.data)
                    } else {
                        uiStateListener.value = state.copy(loadingPage = false)
                        cancelResultListener.emit("")
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
            fetchOrderDetails()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true)
        fetchOrderDetails()
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

    data class OrderDetailsState(
        val orderDetailsUI: OrderDetailsUI? = null
    ) : State
}