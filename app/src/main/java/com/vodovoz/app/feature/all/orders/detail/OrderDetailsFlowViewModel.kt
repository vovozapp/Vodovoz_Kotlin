package com.vodovoz.app.feature.all.orders.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.all.orders.detail.model.DriverPointsEntity
import com.vodovoz.app.mapper.mapToUI
import com.vodovoz.app.ui.model.OrderDetailsUI
import com.vodovoz.app.util.extensions.debugLog
import com.yandex.metrica.YandexMetrica
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val accountManager: AccountManager,
) : PagingStateViewModel<OrderDetailsFlowViewModel.OrderDetailsState>(OrderDetailsState()) {

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    private val orderId = savedState.get<Long>("orderId")

    private val cancelResultListener = MutableSharedFlow<String>()
    fun observeCancelResult() = cancelResultListener.asSharedFlow()

    private fun fetchOrderDetails() {
        val userId = accountManager.fetchAccountId() ?: return
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
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val orderDetails = response.data.mapToUI()
                        if (orderDetails.status?.id == "E" && orderDetails.driverId != null) {
                            checkIfDriverExists(orderDetails.driverId)
                        }
                        uiStateListener.value = state.copy(
                            data = state.data.copy(orderDetailsUI = orderDetails),
                            loadingPage = false,
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
                .catch {
                    debugLog { "fetch order details error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()

        }
    }

    fun repeatOrder() {
        val userId = accountManager.fetchAccountId() ?: return
        val id = orderId ?: return
        uiStateListener.value = state.copy(loadingPage = true, error = null)
        viewModelScope.launch {
            flow {
                emit(
                    repository.repeatOrder(
                        orderId = id,
                        userId = userId
                    )
                )
            }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        cartManager.updateCartListState(true)
                        uiStateListener.value = state.copy(
                            loadingPage = false, error = null,
                            data = state.data.copy(ifRepeatOrder = true)
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
                .catch {
                    debugLog { "repeat order error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun cancelOrder() {
        val id = orderId ?: return
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.cancelOrder(id)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(loadingPage = false)
                        cancelResultListener.emit(response.data)
                    } else {
                        uiStateListener.value = state.copy(loadingPage = false)
                        cancelResultListener.emit("")
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

    private fun checkIfDriverExists(driverId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                firebaseDatabase.child(driverId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list = mutableListOf<DriverPointsEntity?>()
                            snapshot.child("ListTochki").children.forEach {
                                val driverPointsEntity = it.getValue(DriverPointsEntity::class.java)
                                list.add(driverPointsEntity)
                            }

                            val isExists = list.find { it?.OrderNumber == orderId.toString() }

                            if (isExists != null) {
                                uiStateListener.value = state.copy(
                                    data = state.data.copy(
                                        ifDriverExists = true
                                    )
                                )
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }.onFailure {
                debugLog { "checkIfDriverExists error $it" }
                YandexMetrica.reportError("checkIfDriverExists error", it)
            }
        }
    }

    fun repeatOrderFlagReset() {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                ifRepeatOrder = false
            )
        )
    }

    data class OrderDetailsState(
        val orderDetailsUI: OrderDetailsUI? = null,
        val ifDriverExists: Boolean = false,
        val ifRepeatOrder: Boolean = false,
    ) : State
}