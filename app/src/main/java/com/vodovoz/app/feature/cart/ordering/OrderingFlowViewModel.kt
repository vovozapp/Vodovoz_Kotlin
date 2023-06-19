package com.vodovoz.app.feature.cart.ordering

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.config.ShippingAlertConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.ordering.RegOrderResponseJsonParser.parseRegOrderResponse
import com.vodovoz.app.data.parser.response.shipping.FreeShippingDaysResponseJsonParser.parseFreeShippingDaysResponse
import com.vodovoz.app.data.parser.response.shipping.ShippingInfoResponseJsonParser.parseShippingInfoResponse
import com.vodovoz.app.mapper.FreeShippingDaysInfoBundleMapper.mapToUI
import com.vodovoz.app.mapper.OrderingCompletedInfoBundleMapper.mapToUI
import com.vodovoz.app.mapper.ShippingInfoBundleMapper.mapToUI
import com.vodovoz.app.ui.extensions.ContextExtensions.getDeviceInfo
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
import com.vodovoz.app.util.extensions.debugLog
import com.yandex.metrica.YandexMetrica
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
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
    private val cartManager: CartManager,
) : PagingContractViewModel<OrderingFlowViewModel.OrderingState, OrderingFlowViewModel.OrderingEvents>(
    OrderingState(
        full = savedStateHandle.get<Int>("full"),
        deposit = savedStateHandle.get<Int>("deposit"),
        discount = savedStateHandle.get<Int>("discount"),
        total = savedStateHandle.get<Int>("total")
    )
) {

    override val eventListener = MutableSharedFlow<OrderingEvents>(
        replay = 0,
        extraBufferCapacity = 1,
        BufferOverflow.DROP_OLDEST
    )

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private val lastActualCart = savedStateHandle.get<String>("lastActualCart")
    private val coupon = savedStateHandle.get<String>("coupon") ?: ""

    init {
        //accountManager.reportYandexMetrica("Зашел на экран оформления заказа") //todo релиз

        debugLog { "full ${state.data.full}, discount ${state.data.discount}" }
    }

    fun regOrder(
        comment: String = "",
        name: String = "",
        phone: String = "",
        email: String = "",
        companyName: String = "",
        inputCash: String = "",
    ) {
        viewModelScope.launch {

            val userId = accountManager.fetchAccountId() ?: return@launch
            val deviceInfo = application.getDeviceInfo()
            val addressId = state.data.selectedAddressUI?.id
            if (addressId == null) {
                eventListener.emit(OrderingEvents.ChooseAddressError("Выберите адрес!"))
                return@launch
            }

            val selectedDate = state.data.selectedDate

            if (selectedDate == null) {
                eventListener.emit(OrderingEvents.ChooseDateError("Выберите дату!"))
                return@launch
            }

            if (state.data.selectedShippingIntervalUI == null) {
                eventListener.emit(OrderingEvents.ChooseIntervalError("Выберите интервал!"))
                return@launch
            }

            if (state.data.selectedPayMethodUI == null) {
                eventListener.emit(OrderingEvents.ChoosePayMethodError("Выберите способ оплаты!"))
                return@launch
            }

            if (state.data.checkDeliveryValue == 0) {
                eventListener.emit(OrderingEvents.ChooseCheckDeliveryError)
                return@launch
            }

            val needCall = when (state.data.needOperatorCall) {
                true -> "Y"
                false -> "N"
            }

            val totalPrice = state.data.total ?: return@launch
            val depositPrice = state.data.deposit ?: return@launch

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
                        shippingPrice = state.data.shippingPrice,
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
                        overMoney = if (inputCash == "") {
                            0
                        } else {
                            inputCash.toInt()
                        },
                        parking = state.data.shippingInfoBundleUI?.parkingPrice,
                        userId = userId,
                        appVerision = BuildConfig.VERSION_NAME,
                        checkDeliveryValue = state.data.checkDeliveryValue
                    )
                )
            }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseRegOrderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        cartManager.clearCart()
                        cartManager.updateCartListState(true)
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                orderingCompletedInfoBundleUI = data
                            ),
                            loadingPage = false,
                            error = null
                        )
                        //accountManager.reportYandexMetrica("Заказ оформлен")//todo релиз
                        eventListener.emit(OrderingEvents.OrderSuccess(data))
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
                    debugLog { "reg order error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()

        }
    }

    fun checkAddress() {
        viewModelScope.launch {
            val addressId = state.data.selectedAddressUI?.id
            if (addressId == null) {
                eventListener.emit(OrderingEvents.ChooseAddressError("Выберите адрес!"))
                return@launch
            }
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
        val interval =
            state.data.shippingInfoBundleUI?.shippingIntervalUIList?.find { it.id == itemId }
                ?: return

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
        fetchShippingInfo()
    }

    fun setSelectedPaymentMethod(itemId: Long) {
        val method =
            state.data.shippingInfoBundleUI?.payMethodUIList?.find { it.id == itemId } ?: return
        uiStateListener.value = state.copy(
            data = state.data.copy(
                selectedPayMethodUI = method
            )
        )
    }

    fun setSelectedOrderType(type: OrderType) {
        viewModelScope.launch {
            if (state.data.selectedOrderType == type) return@launch
            eventListener.emit(OrderingEvents.ClearFields)
            uiStateListener.value = state.copy(data = OrderingState(selectedOrderType = type, total = state.data.total, full = state.data.full, deposit = state.data.deposit, discount = state.data.discount))
        }
    }

    fun fetchShippingInfo(
        pay: Boolean = false,
        intervals: Boolean = false,
        today: Boolean = false,
    ) {
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
                        date = dateFormatter.format(selectedDate),
                        appVerision = BuildConfig.VERSION_NAME
                    )
                )
            }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseShippingInfoResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        val full = state.data.full
                        val discount = state.data.discount
                        val total = state.data.total
                        val newTotal = if (full != null && discount != null && total != null) {
                            full - discount + data.shippingPrice + data.parkingPrice
                        } else {
                            state.data.total
                        }
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                total = newTotal,
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
                        if (pay) {
                            eventListener.emit(
                                OrderingEvents.ShowPaymentMethod(
                                    data.payMethodUIList,
                                    state.data.selectedPayMethodUI?.id
                                )
                            )
                        }
                        if (intervals) {
                            eventListener.emit(
                                OrderingEvents.ShowShippingIntervals(
                                    data.shippingIntervalUIList,
                                    state.data.selectedDate
                                )
                            )
                        }
                        if (today) {
                            eventListener.emit(OrderingEvents.TodayShippingMessage(data.todayShippingInfo))
                        }
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
                    debugLog { "fetch shipping info error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun fetchFreeShippingDaysInfo() {
        viewModelScope.launch {
            flow { emit(repository.fetchFreeShippingDaysInfoResponse(appVerision = BuildConfig.VERSION_NAME)) }
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
                .catch {
                    debugLog { "fetch free shipping days error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
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

    fun onCheckDeliveryClick() {
        viewModelScope.launch {
            eventListener.emit(OrderingEvents.ShowCheckDeliveryBs(state.data.checkDeliveryValue))
        }
    }

    fun setCheckDelivery(value: Int) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                checkDeliveryValue = value
            )
        )
    }

    data class OrderingState(
        val full: Int? = null,
        val deposit: Int? = null,
        val discount: Int? = null,
        val total: Int? = null,


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

        val orderingCompletedInfoBundleUI: OrderingCompletedInfoBundleUI? = null,
        val checkDeliveryValue: Int = 0,
    ) : State

    sealed class OrderingEvents : Event {
        data class OrderingErrorInfo(val message: String) : OrderingEvents()
        data class OrderSuccess(val item: OrderingCompletedInfoBundleUI) : OrderingEvents()

        data class OnAddressBtnClick(val typeName: String) : OrderingEvents()
        data class OnFreeShippingClick(val bundle: FreeShippingDaysInfoBundleUI) : OrderingEvents()
        object ShowDatePicker : OrderingEvents()
        data class OnShippingAlertClick(val list: List<ShippingAlertUI>) : OrderingEvents()

        data class ShowFreeShippingDaysInfo(val item: FreeShippingDaysInfoBundleUI) :
            OrderingEvents()

        data class ShowPaymentMethod(val list: List<PayMethodUI>, val selectedPayMethodId: Long?) :
            OrderingEvents()

        data class ShowShippingIntervals(
            val list: List<ShippingIntervalUI>,
            val selectedDate: Date?,
        ) : OrderingEvents()

        data class TodayShippingMessage(val message: String) : OrderingEvents()

        data class ChooseAddressError(val message: String) : OrderingEvents()
        data class ChooseDateError(val message: String) : OrderingEvents()
        data class ChooseIntervalError(val message: String) : OrderingEvents()
        data class ChoosePayMethodError(val message: String) : OrderingEvents()
        object ChooseCheckDeliveryError : OrderingEvents()

        data class ShowCheckDeliveryBs(val value: Int) : OrderingEvents()

        object ClearFields : OrderingEvents()
    }
}