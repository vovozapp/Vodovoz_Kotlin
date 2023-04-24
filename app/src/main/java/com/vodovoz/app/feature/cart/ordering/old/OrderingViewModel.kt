package com.vodovoz.app.feature.cart.ordering.old

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.cart.ordering.OrderType
import com.vodovoz.app.mapper.CartBundleMapper.mapUoUI
import com.vodovoz.app.mapper.FreeShippingDaysInfoBundleMapper.mapToUI
import com.vodovoz.app.mapper.OrderingCompletedInfoBundleMapper.mapToUI
import com.vodovoz.app.mapper.ShippingAlertMapper.mapToUI
import com.vodovoz.app.mapper.ShippingInfoBundleMapper.mapToUI

import com.vodovoz.app.ui.base.BaseViewModel
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI
import com.vodovoz.app.util.SingleLiveEvent
import com.vodovoz.app.util.calculatePrice
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OrderingViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : BaseViewModel() {

    private val errorMLD = SingleLiveEvent<String>()
    private val freeShippingDaysInfoMLD = SingleLiveEvent<FreeShippingDaysInfoBundleUI>()
    private val payMethodUIListMLD = SingleLiveEvent<List<PayMethodUI>>()
    private val shippingIntervalUiListMLD = SingleLiveEvent<List<ShippingIntervalUI>>()
    private val fullPriceMLD = MutableLiveData<Int>()
    private val discountPriceMLD = MutableLiveData<Int>()
    private val totalPriceMLD = MutableLiveData<Int>()
    private val depositPriceMLD = MutableLiveData<Int>()
    private val shippingPriceMLD = MutableLiveData<Int>()
    private val parkingPriceMLD = MutableLiveData<Int>()
    private val cartChangeMessageMLD = SingleLiveEvent<String>()
    private val todayShippingMessageMLD = MutableLiveData<String>()
    private val orderingCompletedInfoBundleUIMLD = SingleLiveEvent<OrderingCompletedInfoBundleUI>()

    val errorLD: LiveData<String> = errorMLD
    val freeShippingDaysInfoLD: LiveData<FreeShippingDaysInfoBundleUI> = freeShippingDaysInfoMLD
    val payMethodUIListLD: LiveData<List<PayMethodUI>> = payMethodUIListMLD
    val shippingIntervalUiListLD: LiveData<List<ShippingIntervalUI>> = shippingIntervalUiListMLD
    val fullPriceLD: LiveData<Int> = fullPriceMLD
    val depositPriceLD: LiveData<Int> = depositPriceMLD
    val discountPriceLD: LiveData<Int> = discountPriceMLD
    val totalPriceLD: LiveData<Int> = totalPriceMLD
    val shippingPriceLD: LiveData<Int> = shippingPriceMLD
    val parkingPriceLD: LiveData<Int> = parkingPriceMLD
    val cartChangeMessageLD: LiveData<String> = cartChangeMessageMLD
    val todayShippingMessageLD: LiveData<String> = todayShippingMessageMLD
    val orderingCompletedInfoBundleUILD: LiveData<OrderingCompletedInfoBundleUI> = orderingCompletedInfoBundleUIMLD

    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    var lastActualCart: List<Pair<Long, Int>> = listOf()
        set(value) {
            field = value.sortedBy { it.first }
        }

    var freeShippingDaysInfoBundleUI: FreeShippingDaysInfoBundleUI? = null
    var shippingInfoBundleUI: ShippingInfoBundleUI? = null
    var shippingAlertUIList: List<ShippingAlertUI>? = null
    var orderingCompletedInfoBundleUI: OrderingCompletedInfoBundleUI? = null

    var waitToShowIntervalSelection = false
    var waitToShowPayMethodSelection = false
    var waitToShowTodayShippingInfo = false

    var full: Int = 0
        set(value) {
            field = value
            fullPriceMLD.value = field
        }
    var deposit: Int = 0
        set(value) {
            field = value
            depositPriceMLD.value = field
        }
    var discount: Int = 0
        set(value) {
            field = value
            discountPriceMLD.value = field
        }
    var total: Int = 0
        set(value) {
            field = value
            totalPriceMLD.value = field
        }
    var shippingPrice: Int = 0
        set(value) {
            field = value
            shippingPriceMLD.value = field
            total = full - discount + shippingPrice + parkingPrice
        }

    var parkingPrice: Int = 0
        set(value) {
            field = value
            parkingPriceMLD.value = field
            total = full - discount + shippingPrice + parkingPrice
        }
    var coupon: String = ""

    var selectedOrderType = OrderType.PERSONAL
    var selectedAddressUI: AddressUI? = null
    var selectedPayMethodUI: PayMethodUI? = null
    var selectedDate: Date? = null
    var selectedShippingIntervalUI: ShippingIntervalUI? = null
    var selectedShippingAlertUI: ShippingAlertUI? = null
    var needOperatorCall = false
    var comment = ""
    var name = ""
    var phone = ""
    var email = ""
    var companyName = ""
    var inputCash = 0

    fun updateArgs(
        full: Int,
        deposit: Int,
        discount: Int,
        total: Int,
        lastActualCart: String,
        coupon: String
    ) {
        this.coupon = coupon
        this.full = full
        this.deposit = deposit
        this.total = total
        this.discount = discount
        fullPriceMLD.value = full
        depositPriceMLD.value = deposit
        totalPriceMLD.value = total
        discountPriceMLD.value = discount

        val cart = mutableListOf<Pair<Long, Int>>()
        val productList = lastActualCart.split(",")
        for (product in productList) {
            if (product.isNotEmpty()) {
                val productSplit = product.split(":")
                cart.add(Pair(productSplit.first().toLong(), productSplit.last().toInt()))
            }
        }
        this.lastActualCart = cart
    }

    fun fetchFreeShippingDaysInfo() {
        dataRepository
            .fetchFreeShippingDaysInfo()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Error -> errorMLD.value = it.errorMessage
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Success -> {
                            freeShippingDaysInfoBundleUI = it.data.mapToUI()
                            freeShippingDaysInfoMLD.value = freeShippingDaysInfoBundleUI
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка"}
            ).addTo(compositeDisposable)
    }

    fun fetchShippingInfo() {
        if (selectedAddressUI == null) {
            errorMLD.value = "Выберите адрес!"
            waitToShowTodayShippingInfo = false
            waitToShowIntervalSelection = false
            waitToShowPayMethodSelection = false
            return
        }
        dataRepository
            .fetchShippingInfo(
                addressId = selectedAddressUI?.id,
                date = when(selectedDate) {
                    null -> ""
                    else -> dateFormatter.format(selectedDate!!)
                }
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Error -> errorMLD.value = it.errorMessage
                        is ResponseEntity.Success -> {
                            val data = it.data.mapToUI()
                            shippingPrice = data.shippingPrice
                            parkingPrice = data.parkingPrice
                            payMethodUIListMLD.value = data.payMethodUIList
                            shippingIntervalUiListMLD.value = data.shippingIntervalUIList
                            if (data.todayShippingInfo.isNotEmpty()) todayShippingMessageMLD.value = data.todayShippingInfo
                            shippingInfoBundleUI = data
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" }
            ).addTo(compositeDisposable)
    }

    fun checkActualCart(deviceInfo: String) {
        dataRepository.fetchCart()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка!"
                        is ResponseEntity.Error -> errorMLD.value = response.errorMessage
                        is ResponseEntity.Success -> {
                            response.data.let { data ->
                                val actualCart = data.mapUoUI().availableProductUIList
                                    .map { Pair(it.id, it.cartQuantity) }
                                    .sortedBy { it.first }
                                var isCartChange = false
                                for (index in lastActualCart.indices) {
                                    if (lastActualCart[index].first != actualCart[index].first
                                        || lastActualCart[index].second != actualCart[index].second
                                    ) {
                                        isCartChange = true
                                        break
                                    }
                                }
                                when (isCartChange) {
                                    true -> {
                                        val prices = calculatePrice(data.mapUoUI().availableProductUIList)
                                        full = prices.fullPrice
                                        discount = prices.discountPrice
                                        total = prices.fullPrice + prices.deposit - prices.discountPrice
                                        deposit = prices.deposit
                                        cartChangeMessageMLD.value = "Похоже, что некоторых продуктов из вашей корзины больше нет в наличии, поэтому мы убрали их из заказа."
                                    }
                                    else -> regOrder(deviceInfo)
                                }
                            }
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка!" }
            ).addTo(compositeDisposable)
    }

    fun regOrder(
        deviceInfo: String
    ) {
        dataRepository
            .regOrder(
                orderType = selectedOrderType.value,
                device = deviceInfo,
                addressId = selectedAddressUI?.id,
                date = dateFormatter.format(selectedDate!!),
                paymentId = selectedPayMethodUI?.id,
                needOperatorCall = when(needOperatorCall) {
                    true -> "Y"
                    false -> "N"
                },
                needShippingAlert = selectedShippingAlertUI?.name,
                comment = comment,
                totalPrice = total,
                shippingId = shippingInfoBundleUI?.id,
                shippingPrice = shippingInfoBundleUI?.shippingPrice,
                name = name,
                phone = phone,
                email = email,
                companyName = companyName,
                deposit = deposit,
                fastShippingPrice = shippingInfoBundleUI?.todayShippingPrice,
                extraShippingPrice = shippingInfoBundleUI?.extraShippingPrice,
                commonShippingPrice = shippingInfoBundleUI?.commonShippingPrice,
                coupon = coupon,
                shippingIntervalId = selectedShippingIntervalUI?.id,
                overMoney = inputCash,
                parking = shippingInfoBundleUI?.parkingPrice
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Error -> errorMLD.value = it.errorMessage
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Success -> {
                            dataRepository.clearCart()
                            orderingCompletedInfoBundleUI = it.data.mapToUI()
                            orderingCompletedInfoBundleUIMLD.value = orderingCompletedInfoBundleUI
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" }
            ).addTo(compositeDisposable)
    }
    fun fetchShippingAlertsList(): List<ShippingAlertUI> {
        shippingAlertUIList = dataRepository.fetchShippingAlertEntityList().mapToUI()
        return shippingAlertUIList!!
    }

    fun clearData() {
        selectedDate = null
        selectedAddressUI = null
        selectedShippingIntervalUI = null
        selectedPayMethodUI = null
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}