package com.vodovoz.app.ui.components.fragment.bottom_dialog_add_address

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.AddressConfig
import com.vodovoz.app.data.config.FieldValidateConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.BottomFragmentSortSettingsBinding
import com.vodovoz.app.databinding.DialogBottomAddAddressBinding
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.mapper.AddressMapper.mapToUI
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.Keys
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception

class AddAddressViewModel(
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
        Log.i(LogSettings.ID_LOG, "START")
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
        null -> dataRepository
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
        else -> dataRepository
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

    private fun validateLocality() = FieldValidateConfig.LOCALITY_LENGTH.contains(locality.length).apply {
        localityErrorMLD.value =
            if (!this && !isFirstTry) "Длина ${FieldValidateConfig.LOCALITY_LENGTH}"
            else ""
    }

    private fun validateStreet() = FieldValidateConfig.STREET_LENGTH.contains(street.length).apply {
        streetErrorMLD.value =
            if (!this && !isFirstTry) "Длина ${FieldValidateConfig.STREET_LENGTH}"
            else ""
    }

    private fun validateHouse() = FieldValidateConfig.HOUSE_LENGTH.contains(house.length).apply {
        houseErrorMLD.value =
            if (!this && !isFirstTry) "Длина ${FieldValidateConfig.HOUSE_LENGTH}"
            else ""
    }

    private fun validateEntrance() = when(type) {
        AddressConfig.OFFICE_ADDRESS_TYPE -> FieldValidateConfig.ENTRANCE_LENGTH.contains(entrance.length).apply {
            entranceErrorMLD.value =
                if (!this && !isFirstTry) "Длина ${FieldValidateConfig.ENTRANCE_LENGTH}"
                else ""
        }
        else -> {
            entranceErrorMLD.value = ""
            true
        }
    }

    private fun validateFloor() = when(type) {
        AddressConfig.OFFICE_ADDRESS_TYPE -> FieldValidateConfig.FLOOR_LENGTH.contains(floor.length).apply {
            floorErrorMLD.value =
                if (!this && !isFirstTry) "Длина ${FieldValidateConfig.FLOOR_LENGTH}"
                else ""
        }
        else -> {
            floorErrorMLD.value = ""
            true
        }
    }

    private fun validateOffice() = when(type) {
        AddressConfig.OFFICE_ADDRESS_TYPE -> FieldValidateConfig.OFFICE_LENGTH.contains(office.length).apply {
            officeErrorMLD.value =
                if (!this && !isFirstTry) "Длина ${FieldValidateConfig.OFFICE_LENGTH}"
                else ""
        }
        else -> {
            officeErrorMLD.value = ""
            true
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}