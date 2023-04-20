package com.vodovoz.app.feature.addresses.add.old

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.AddressConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.FieldValidationsSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val addressUIMLD = MutableLiveData<AddressUI>()
    private val localityErrorMLD = MutableLiveData<String>()
    private val streetErrorMLD = MutableLiveData<String>()
    private val houseErrorMLD = MutableLiveData<String>()
    private val entranceErrorMLD = MutableLiveData<String>()
    private val floorErrorMLD = MutableLiveData<String>()
    private val officeErrorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val addressLD: LiveData<AddressUI> = addressUIMLD
    val localityLD: LiveData<String> = localityErrorMLD
    val streetLD: LiveData<String> = streetErrorMLD
    val houseLD: LiveData<String> = houseErrorMLD
    val entranceLD: LiveData<String> = entranceErrorMLD
    val floorLD: LiveData<String> = floorErrorMLD
    val officeLD: LiveData<String> = officeErrorMLD

    private val compositeDisposable = CompositeDisposable()
    var addressUI: AddressUI? = null

    private var isFirstTry = true
    var type: Int = 1

    var locality = ""
        set(value) {
            field = value
            validateLocality()
        }
    var street = ""
        set(value) {
            field = value
            validateStreet()
        }
    var house = ""
        set(value) {
            field = value
            validateHouse()
        }
    var entrance = ""
        set(value) {
            field = value
            validateEntrance()
        }
    var floor = ""
        set(value) {
            field = value
            validateFloor()
        }

    var office = ""
        set(value) {
            field = value
            validateOffice()
        }

    var comment = ""


    fun updateArgs(addressUI: AddressUI?) {
        this.addressUI = addressUI
        addressUI?.let { addressUIMLD.value = it }
    }

    fun validate(type: Int) {
        this.type = type
        this.isFirstTry = false

        var isValid = true

        if (!validateLocality()) isValid = false
        if (!validateStreet()) isValid = false
        if (!validateHouse()) isValid = false
        if (!validateEntrance()) isValid = false
        if (!validateFloor()) isValid = false
        if (!validateOffice()) isValid = false


        if (isValid) addAddressOrUpdateAddress()
    }

    private fun addAddressOrUpdateAddress() {
        getSingle(addressUI?.id)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Неизсвестная ошибка")
                        is ResponseEntity.Success -> {
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизсвестная ошибка") }
            ).addTo(compositeDisposable)
    }

    private fun getSingle(id: Long?) = when(id) {
        null, 0L -> {
            dataRepository
                .addAddress(
                    locality = locality,
                    street = street,
                    house = house,
                    entrance = entrance,
                    floor = floor,
                    office = office,
                    comment = comment,
                    type = type,
                )
        }
        else -> {
            dataRepository
                .updateAddress(
                    addressId = addressUI?.id,
                    locality = locality,
                    street = street,
                    house = house,
                    entrance = entrance,
                    floor = floor,
                    office = office,
                    comment = comment,
                    type = type,
                )
        }
    }

    private fun validateLocality() = FieldValidationsSettings.LOCALITY_LENGTH.contains(locality.length).apply {
        localityErrorMLD.value =
            if (!this && !isFirstTry) "Длина ${FieldValidationsSettings.LOCALITY_LENGTH}"
            else ""
    }

    private fun validateStreet() = FieldValidationsSettings.STREET_LENGTH.contains(street.length).apply {
        streetErrorMLD.value =
            if (!this && !isFirstTry) "Длина ${FieldValidationsSettings.STREET_LENGTH}"
            else ""
    }

    private fun validateHouse() = FieldValidationsSettings.HOUSE_LENGTH.contains(house.length).apply {
        houseErrorMLD.value =
            if (!this && !isFirstTry) "Длина ${FieldValidationsSettings.HOUSE_LENGTH}"
            else ""
    }

    private fun validateEntrance() = when(type) {
        AddressConfig.OFFICE_ADDRESS_TYPE -> FieldValidationsSettings.ENTRANCE_LENGTH.contains(entrance.length).apply {
            entranceErrorMLD.value =
                if (!this && !isFirstTry) "Длина ${FieldValidationsSettings.ENTRANCE_LENGTH}"
                else ""
        }
        else -> {
            entranceErrorMLD.value = ""
            true
        }
    }

    private fun validateFloor() = when(type) {
        AddressConfig.OFFICE_ADDRESS_TYPE -> FieldValidationsSettings.FLOOR_LENGTH.contains(floor.length).apply {
            floorErrorMLD.value =
                if (!this && !isFirstTry) "Длина ${FieldValidationsSettings.FLOOR_LENGTH}"
                else ""
        }
        else -> {
            floorErrorMLD.value = ""
            true
        }
    }

    private fun validateOffice() = when(type) {
        AddressConfig.OFFICE_ADDRESS_TYPE -> FieldValidationsSettings.OFFICE_LENGTH.contains(office.length).apply {
            officeErrorMLD.value =
                if (!this && !isFirstTry) "Длина ${FieldValidationsSettings.OFFICE_LENGTH}"
                else ""
        }
        else -> {
            officeErrorMLD.value = ""
            true
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}