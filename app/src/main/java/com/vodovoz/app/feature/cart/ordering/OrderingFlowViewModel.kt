package com.vodovoz.app.feature.cart.ordering

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.config.ShippingAlertConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.map.FetchAddressesSavedResponseJsonParser.parseFetchAddressesSavedResponse
import com.vodovoz.app.data.parser.response.ordering.RegOrderResponseJsonParser.parseRegOrderResponse
import com.vodovoz.app.data.parser.response.shipping.FreeShippingDaysResponseJsonParser.parseFreeShippingDaysResponse
import com.vodovoz.app.data.parser.response.shipping.ShippingInfoResponseJsonParser.parseShippingInfoResponse
import com.vodovoz.app.mapper.AddressMapper.mapToUI
import com.vodovoz.app.mapper.FreeShippingDaysInfoBundleMapper.mapToUI
import com.vodovoz.app.mapper.OrderingCompletedInfoBundleMapper.mapToUI
import com.vodovoz.app.mapper.ShippingInfoBundleMapper.mapToUI
import com.vodovoz.app.ui.extensions.ContextExtensions.getDeviceInfo
import com.vodovoz.app.ui.fragment.ordering.OrderType
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrderingFlowViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val application: Application,
    private val cartManager: CartManager
) : PagingContractViewModel<OrderingFlowViewModel.OrderingState, OrderingFlowViewModel.OrderingEvents>(
    OrderingState()
) {

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private val fullPrice = savedStateHandle.get<Int>("full")
    private val deposit = savedStateHandle.get<Int>("deposit")
    private val discount = savedStateHandle.get<Int>("discount")
    private val total = savedStateHandle.get<Int>("total")
    private val lastActualCart = savedStateHandle.get<String>("lastActualCart")
    private val coupon = savedStateHandle.get<String>("coupon") ?: ""

    fun regOrder(
        comment: String = "",
        name: String = "",
        phone: String = "",
        email: String = "",
        companyName: String = "",
        inputCash: Int = 0,
    ) {
        viewModelScope.launch {

            val userId = accountManager.fetchAccountId() ?: return@launch
            val deviceInfo = application.getDeviceInfo()
            val addressId = state.data.selectedAddressUI?.id
            if (addressId == null) {
                eventListener.emit(OrderingEvents.OrderingErrorInfo("Выберите адрес!"))
                return@launch
            }

            val selectedDate = state.data.selectedDate

            if (selectedDate == null) {
                eventListener.emit(OrderingEvents.OrderingErrorInfo("Выберите дату!"))
                return@launch
            }

            val needCall = when(state.data.needOperatorCall) {
                true -> "Y"
                false -> "N"
            }

            val totalPrice = total ?: return@launch
            val depositPrice = deposit ?: return@launch

            flow {
                emit(
                    repository.regOrder(
                        orderType = state.data.selectedOrderType.value,
                        device = deviceInfo,
                        addressId = addressId,
                        date = dateFormatter.format(selectedDate),
                        paymentId = state.data.selectedPayMethodUI?.id,
                        needOperatorCall = needCall,
                        needShippingAlert = state.data.selectedShippingAlertUI?.name,
                        comment = comment,
                        totalPrice = totalPrice,
                        shippingId = state.data.shippingInfoBundleUI?.id,
                        shippingPrice = state.data.shippingInfoBundleUI?.shippingPrice,
                        name = name,
                        phone = phone,
                        email = email,
                        companyName = companyName,
                        deposit = depositPrice,
                        fastShippingPrice = state.data.shippingInfoBundleUI?.todayShippingPrice,
                        extraShippingPrice = state.data.shippingInfoBundleUI?.extraShippingPrice,
                        commonShippingPrice = state.data.shippingInfoBundleUI?.commonShippingPrice,
                        coupon = coupon,
                        shippingIntervalId = state.data.selectedShippingIntervalUI?.id,
                        overMoney = inputCash,
                        parking = state.data.shippingInfoBundleUI?.parkingPrice,
                        userId = userId
                    )
                )
            }
                .catch {
                    debugLog { "reg order error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseRegOrderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        //todo clear cart
                        cartManager.clearCart()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                orderingCompletedInfoBundleUI = data
                            ),
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
                .collect()

        }
    }

    fun setSelectedDate(date: Date) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                selectedDate = date
            )
        )
    }

    fun setSelectedShippingInterval(itemId: Long) {
        val interval = state.data.shippingInfoBundleUI?.shippingIntervalUIList?.find { it.id == itemId } ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                selectedShippingIntervalUI = interval
            )
        )
    }

    fun setSelectedShippingAlert(itemId: Long) {

        val alert = state.data.shippingAlertList.find { it.id == itemId } ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                selectedShippingAlertUI = alert
            )
        )
    }

    fun setNeedOperatorCall(bool: Boolean) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                needOperatorCall = bool
            )
        )
    }

    fun setSelectedAddress(item: AddressUI) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                selectedAddressUI = item
            )
        )
    }

    fun setSelectedPaymentMethod(itemId: Long) {
        val method = state.data.shippingInfoBundleUI?.payMethodUIList?.find { it.id == itemId } ?: return
        uiStateListener.value = state.copy(
            data = state.data.copy(
                selectedPayMethodUI = method
            )
        )
    }

    fun setSelectedOrderType(type: OrderType) {
        if (state.data.selectedOrderType == type) return
        clearData()
        uiStateListener.value = if (type == OrderType.PERSONAL) {
            state.copy(
                data = state.data.copy(
                    selectedOrderType = OrderType.PERSONAL
                )
            )
        } else {
            state.copy(
                data = state.data.copy(
                    selectedOrderType = OrderType.COMPANY
                )
            )
        }
    }

    fun fetchShippingInfo() {
        viewModelScope.launch {
            val address = state.data.selectedAddressUI
            if (address == null) {
                eventListener.emit(OrderingEvents.OrderingErrorInfo("Выберите адрес!"))
                return@launch
            }

            val selectedDate = state.data.selectedDate

            if (selectedDate == null) {
                eventListener.emit(OrderingEvents.OrderingErrorInfo("Выберите дату!"))
                return@launch
            }

            val userId = accountManager.fetchAccountId() ?: return@launch


            flow {
                emit(
                    repository.fetchShippingInfo(
                        addressId = address.id,
                        userId = userId,
                        date = dateFormatter.format(selectedDate)
                    )
                )
            }
                .catch {
                    debugLog { "fetch shipping info error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseShippingInfoResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                shippingInfoBundleUI = data,
                                shippingPrice = data.shippingPrice,
                                parkingPrice = data.parkingPrice,
                                payMethodList = data.payMethodUIList,
                                shippingIntervalUiList = data.shippingIntervalUIList,
                                todayShippingInfo = data.todayShippingInfo.takeIf { it.isNotEmpty() }
                            ),
                            loadingPage = false,
                            error = null
                        )
                        eventListener.emit(OrderingEvents.ShowPaymentMethod(data.payMethodUIList, state.data.selectedPayMethodUI?.id))
                        //todo
                        eventListener.emit(OrderingEvents.ShowShippingIntervals(data.shippingIntervalUIList, state.data.selectedDate))
                        //todo
                        eventListener.emit(OrderingEvents.TodayShippingMessage(data.todayShippingInfo))
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

    fun fetchFreeShippingDaysInfo() {
        viewModelScope.launch {
            flow { emit(repository.fetchFreeShippingDaysInfoResponse()) }
                .catch {
                    debugLog { "fetch free shipping days error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseFreeShippingDaysResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                shippingDaysInfoBundleUi = data
                            ),
                            loadingPage = false,
                            error = null
                        )
                        eventListener.emit(OrderingEvents.ShowFreeShippingDaysInfo(data))
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

    fun clearData() {
        uiStateListener.value = state.copy(
            data = OrderingState()
        )
    }

    fun onAddressBtnClick() {
        viewModelScope.launch {
            eventListener.emit(OrderingEvents.OnAddressBtnClick(state.data.selectedOrderType.name))
        }
    }

    fun onFreeShippingClick() {
        viewModelScope.launch {
            val bundle = state.data.shippingDaysInfoBundleUi
            if (bundle == null) {
                fetchFreeShippingDaysInfo()
            } else {
                eventListener.emit(OrderingEvents.OnFreeShippingClick(bundle))
            }
        }
    }

    fun onDateClick() {
        viewModelScope.launch {
            if (state.data.selectedAddressUI == null) {
                eventListener.emit(OrderingEvents.OrderingErrorInfo("Выберите адрес!"))
            } else {
                eventListener.emit(OrderingEvents.ShowDatePicker)
            }
        }
    }

    fun onShippingAlertClick() {
        viewModelScope.launch {
            eventListener.emit(OrderingEvents.OnShippingAlertClick(state.data.shippingAlertList))
        }
    }

    data class OrderingState(
        val selectedAddressUI: AddressUI? = null,
        val selectedDate: Date? = null,
        val selectedOrderType: OrderType = OrderType.PERSONAL,
        val selectedPayMethodUI: PayMethodUI? = null,
        val selectedShippingIntervalUI: ShippingIntervalUI? = null,
        val selectedShippingAlertUI: ShippingAlertUI? = null,
        val needOperatorCall: Boolean = false,

        val shippingPrice: Int? = null,
        val parkingPrice: Int? = null,
        val payMethodList: List<PayMethodUI>? = null,
        val todayShippingInfo: String? = null,
        val shippingInfoBundleUI: ShippingInfoBundleUI? = null,
        val shippingIntervalUiList: List<ShippingIntervalUI>? = null,
        val shippingAlertList: List<ShippingAlertUI> = ShippingAlertConfig.shippingAlertEntityListUI,
        val shippingDaysInfoBundleUi: FreeShippingDaysInfoBundleUI? = null,

        val orderingCompletedInfoBundleUI: OrderingCompletedInfoBundleUI? = null
    ) : State

    sealed class OrderingEvents : Event {
        data class OrderingErrorInfo(val message: String) : OrderingEvents()
        data class OrderSuccess(val item: OrderingCompletedInfoBundleUI) : OrderingEvents()

        data class OnAddressBtnClick(val typeName: String): OrderingEvents()
        data class OnFreeShippingClick(val bundle: FreeShippingDaysInfoBundleUI): OrderingEvents()
        object ShowDatePicker : OrderingEvents()
        data class OnShippingAlertClick(val list: List<ShippingAlertUI>): OrderingEvents()

        data class ShowFreeShippingDaysInfo(val item : FreeShippingDaysInfoBundleUI) : OrderingEvents()
        data class ShowPaymentMethod(val list : List<PayMethodUI>, val selectedPayMethodId: Long?) : OrderingEvents()
        data class ShowShippingIntervals(val list : List<ShippingIntervalUI>, val selectedDate: Date?) : OrderingEvents()
        data class TodayShippingMessage(val message: String) : OrderingEvents()
    }
}