package com.vodovoz.app.ui.fragment.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.mapper.AddressMapper.mapToUI
import com.vodovoz.app.mapper.DeliveryZonesBundleMapper.mapToUI
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.ui.model.DeliveryZoneUI
import com.vodovoz.app.ui.model.custom.DeliveryZonesBundleUI
import com.vodovoz.app.util.Keys
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers


class MapViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val deliveryZoneUIListMLD = MutableLiveData<List<DeliveryZoneUI>>()
    private val addressUIMLD = MutableLiveData<AddressUI>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val deliveryZoneUIListLD: LiveData<List<DeliveryZoneUI>> = deliveryZoneUIListMLD
    val addressUILD: LiveData<AddressUI> = addressUIMLD
    val errorLD: LiveData<String> = errorMLD

    private val compositeDisposable = CompositeDisposable()

    private lateinit var deliveryZonesBundleUI: DeliveryZonesBundleUI
    var addressUI: AddressUI? = null

    fun updateArgs(addressUI: AddressUI?) {
        this.addressUI = addressUI
        addressUI?.let { addressUIMLD.value = it }
    }

    fun updateData() {
        dataRepository
            .fetchDeliveryZonesBundle()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Hide")
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            deliveryZonesBundleUI = response.data.mapToUI()
                            deliveryZoneUIListMLD.value = deliveryZonesBundleUI.deliveryZoneUIList
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная") }
            ).addTo(compositeDisposable)

    }

    fun fetchAddressByGeocode(
        latitude: Double,
        longitude: Double
    ) {
        dataRepository
            .fetchAddressByGeocode(
                latitude = latitude,
                longitude = longitude,
                apiKey = Keys.MAPKIT_API_KEY
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    val addressId = addressUI?.id ?: 0
                    addressUI = (response as ResponseEntity.Success).data.mapToUI()
                    addressUI?.id = addressId
                    addressUIMLD.value = addressUI
                },
                onError = { throwable -> errorMLD.value = throwable.message ?: "Неизвестная ошибка"}
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}