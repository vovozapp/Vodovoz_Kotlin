package com.vodovoz.app.ui.fragment.ordering

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.FreeShippingDaysInfoBundleMapper.mapToUI
import com.vodovoz.app.mapper.PayMethodMapper.mapToUI
import com.vodovoz.app.mapper.ShippingIntervalMapper.mapToUI
import com.vodovoz.app.ui.adapter.FormField
import com.vodovoz.app.ui.adapter.ShippingIntervalVH

import com.vodovoz.app.ui.base.BaseViewModel
import com.vodovoz.app.ui.extensions.Date.dd
import com.vodovoz.app.ui.extensions.Date.get
import com.vodovoz.app.ui.extensions.Date.mm
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.FreeShippingDaysInfoBundleUI
import com.vodovoz.app.ui.model.PayMethodUI
import com.vodovoz.app.ui.model.ShippingIntervalUI
import com.vodovoz.app.util.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class OrderingViewModel(
    private val dataRepository: DataRepository
) : BaseViewModel() {

    private val errorMLD = SingleLiveEvent<String>()
    private val freeShippingDaysInfoMLD = MutableLiveData<FreeShippingDaysInfoBundleUI>()
    private val payMethodUIListMLD = MutableLiveData<List<PayMethodUI>>()
    private val shippingIntervalUiListMLD = MutableLiveData<List<ShippingIntervalUI>>()

    val errorLD: LiveData<String> = errorMLD
    val freeShippingDaysInfoLD: LiveData<FreeShippingDaysInfoBundleUI> = freeShippingDaysInfoMLD
    val payMethodUIListLD: LiveData<List<PayMethodUI>> = payMethodUIListMLD
    val shippingIntervalUiListLD: LiveData<List<ShippingIntervalUI>> = shippingIntervalUiListMLD

    var freeShippingDaysInfoBundleUI: FreeShippingDaysInfoBundleUI? = null
    var payMethodUIList: List<PayMethodUI>? = null
    var shippingIntervalUIList: List<ShippingIntervalUI>? = null

    var orderType = OrderType.PERSONAL
    var selectedAddressUI: AddressUI? = null
    var selectedPayMethodUI: PayMethodUI? = null
    var selectedDate: Date? = null
    var selectedShippingIntervalUI: ShippingIntervalUI? = null

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
            )
    }

    fun fetchPayMethods() {
        if (selectedAddressUI == null) {
            errorMLD.value = "Выберите адрес!"
            return
        }
        dataRepository
            .fetchPayMethods(selectedAddressUI?.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Error -> errorMLD.value = it.errorMessage
                        is ResponseEntity.Success -> {
                            payMethodUIList = it.data.mapToUI()
                            payMethodUIListMLD.value = payMethodUIList
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" }
            )
    }

    fun fetchShippingIntervalList() {
        if (selectedAddressUI == null) {
            errorMLD.value = "Выберите адрес!"
            return
        }
        if (selectedDate == null) {
            errorMLD.value = "Выберите дату!"
            return
        }
        val date = "${selectedDate!!.dd()}.${selectedDate!!.mm()}.${selectedDate!!.get(Calendar.YEAR)}"
        dataRepository
            .fetchShippingIntervals(
                addressId = selectedAddressUI?.id,
                date = date
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Error -> errorMLD.value = it.errorMessage
                        is ResponseEntity.Success -> {
                            shippingIntervalUIList = it.data.mapToUI()
                            shippingIntervalUiListMLD.value = shippingIntervalUIList
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" }
            )
    }

    fun clearData() {
        selectedDate = null
        selectedAddressUI = null
        selectedShippingIntervalUI = null
        selectedPayMethodUI = null
    }

}