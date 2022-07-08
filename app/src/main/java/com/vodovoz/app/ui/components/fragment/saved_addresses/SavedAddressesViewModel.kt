package com.vodovoz.app.ui.components.fragment.saved_addresses

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ReportFragment
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.AddressConfig
import com.vodovoz.app.data.model.common.AddressEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseDialogFragment
import com.vodovoz.app.ui.mapper.AddressMapper.mapToUI
import com.vodovoz.app.ui.model.AddressUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class SavedAddressesViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val officeAddressUIListMLD = MutableLiveData<List<AddressUI>>()
    private val personalAddressUIListMLD = MutableLiveData<List<AddressUI>>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val officeAddressUIListLD: LiveData<List<AddressUI>> = officeAddressUIListMLD
    val personalAddressUIListLD: LiveData<List<AddressUI>> = personalAddressUIListMLD
    val errorLD: LiveData<String> = errorMLD

    private val compositeDisposable = CompositeDisposable()
    private var isUpdateSuccess = false

    fun updateData() {
        dataRepository
            .fetchAddressesSaved(null)
            .doOnSubscribe { if (!isUpdateSuccess) viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                     when(response) {
                         is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                         is ResponseEntity.Error -> {
                             when (isUpdateSuccess) {
                                 false -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                                 true -> errorMLD.value = response.errorMessage
                             }
                         }
                         is ResponseEntity.Success -> {
                             isUpdateSuccess = true
                             val data = response.data.mapToUI()
                             officeAddressUIListMLD.value = data.filter { it.type == AddressConfig.OFFICE_ADDRESS_TYPE }
                             personalAddressUIListMLD.value = data.filter { it.type == AddressConfig.PERSONAL_ADDRESS_TYPE }
                             viewStateMLD.value = ViewState.Success()
                         }
                     }
                },
                onError = { throwable ->
                    when (isUpdateSuccess) {
                        false -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка!")
                        true -> errorMLD.value = throwable.message ?: "Неизвестная ошибка!"
                    }
                }
            ).addTo(compositeDisposable)
    }

    fun deleteAddress(addressId: Long) {
        dataRepository
            .deleteAddress(addressId = addressId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка!"
                        is ResponseEntity.Error -> errorMLD.value = response.errorMessage
                        is ResponseEntity.Success -> {
                            updateData()
                        }
                    }
                },
                onError = { throwable -> errorMLD.value = throwable.message ?: "Неизвестная ошибка!" }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable
    }

}