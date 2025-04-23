package com.vodovoz.app.feature.all.orders.detail

import androidx.compose.runtime.Immutable
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
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.OrderProductUi
import com.vodovoz.app.design_system.model.mapToUi
import com.vodovoz.app.domain.general.model.order.composables.OrderDetailsButtonUi
import com.vodovoz.app.domain.general.model.order.composables.mapToUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.all.orders.detail.composables.AboutOrderPopupWindowUi
import com.vodovoz.app.feature.all.orders.detail.model.DriverPointsEntity
import com.vodovoz.app.feature.all.orders.detail.model.OrderDetailsSummaryUi
import com.vodovoz.app.feature.all.orders.detail.model.OrderStatusUi
import com.vodovoz.app.feature.all.orders.detail.model.mapToUi
import com.vodovoz.app.feature.all.orders.detail.model.toUi
import com.vodovoz.app.mapper.mapToUI
import com.vodovoz.app.ui.model.OrderDetailsUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<OrderDetailsFlowViewModel.OrderDetailsState, OrderDetailsFlowViewModel.OrderDetailsEvent>(
    OrderDetailsState()
) {

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    private val orderId = savedState.get<Long>("orderId")

    private val cancelResultListener = MutableSharedFlow<String>()
    fun observeCancelResult() = cancelResultListener.asSharedFlow()

    init {
        fetchOrderDetails()
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(OrderDetailsEvent.GoBack)
    }

    fun copyOrderId() = viewModelScope.launch {
        eventListener.emit(OrderDetailsEvent.CopyText(orderId.toString()))
    }

    fun fetchOrderDetails() = viewModelScope.launch {

        val orderDetailsResult =
            vodovozServiceRepository.getOrderDetails(orderId ?: return@launch).singleResult()

        orderDetailsResult.onSuccess { orderDetails ->

            uiStateListener.updateData {
                it.copy(
                    topButtons = orderDetails.topButtons.mapToUi(),
                    bottomButtons = orderDetails.bottomButtons.mapToUi(),
                    title = orderDetails.title,
                    subtitle = orderDetails.subtitle,
                    //todo - put of backend
                    header = "",
                    orderSummary = orderDetails.orderSummary.toUi(),
                    statuses = orderDetails.statuses.mapToUi(),
                    currentStatus = orderDetails.currentStatus.toUi(),
                    productsTitle = orderDetails.productsTitle,
                    products = orderDetails.products.mapToUi()
                )
            }

        }.onFailure { t ->
            //todo - handle error
        }
    }

    private fun fetchOrderDetailsOld() {
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
            fetchOrderDetailsOld()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true)
        fetchOrderDetailsOld()
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
                accountManager.reportError("checkIfDriverExists error", it)
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

    fun postUrl(url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        viewModelScope.launch {
            repository.postUrl(url)
        }
    }

    fun activateTopButton(orderDetailsButton: OrderDetailsButtonUi) = viewModelScope.launch {
        when (orderDetailsButton) {
            is OrderDetailsButtonUi.AboutOrderButton -> {
                showAboutOrderBottomSheet(orderDetailsButton.popupWindow)
            }

            is OrderDetailsButtonUi.ImageButton -> {

            }
            is OrderDetailsButtonUi.PayButton -> {

            }
            is OrderDetailsButtonUi.TipsButton -> {

            }
            is OrderDetailsButtonUi.WhereOrderButton -> {

            }
        }
    }


    fun activateBottomButton(button: ColorfulButtonUi) = viewModelScope.launch {

    }

    fun changeProductFavorite(orderProduct: OrderProductUi) = viewModelScope.launch {

    }

    fun navigateToProductDetails(orderProduct: OrderProductUi) = viewModelScope.launch {

    }

    fun showAboutOrderBottomSheet(aboutOrderBottomSheet: AboutOrderPopupWindowUi) =
        viewModelScope.launch {
            uiStateListener.updateData { s ->
                s.copy(
                    showAboutOrderBS = true,
                    currentAboutOrderBS = aboutOrderBottomSheet
                )
            }
        }

    fun closeAboutOrderBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showAboutOrderBS = false)
        }
    }

    @Immutable
    data class OrderDetailsState(
        val orderDetailsUI: OrderDetailsUI? = null,
        val ifDriverExists: Boolean = false,
        val ifRepeatOrder: Boolean = false,

        val title: String = "",
        val subtitle: String = "",
        val header: String = "",
        val topButtons: List<OrderDetailsButtonUi> = emptyList(),
        val bottomButtons: List<ColorfulButtonUi> = emptyList(),
        val orderSummary: OrderDetailsSummaryUi = OrderDetailsSummaryUi.Empty,
        val currentStatus: OrderStatusUi = OrderStatusUi.Empty,
        val statuses: List<OrderStatusUi> = emptyList(),
        val productsTitle: String = "",
        val products: List<OrderProductUi> = emptyList(),
        val currentAboutOrderBS: AboutOrderPopupWindowUi? = null,
        val showAboutOrderBS: Boolean = false,
    ) : State

    sealed class OrderDetailsEvent : Event {
        data object GoBack : OrderDetailsEvent()

        data class CopyText(val text: String) : OrderDetailsEvent()
    }
}