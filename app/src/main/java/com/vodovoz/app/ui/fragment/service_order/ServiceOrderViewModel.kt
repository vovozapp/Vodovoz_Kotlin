package com.vodovoz.app.ui.fragment.service_order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.ServiceOrderFormFieldMapper.mapToUI
import com.vodovoz.app.ui.base.BaseViewModel
import com.vodovoz.app.ui.model.ServiceOrderFormFieldUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ServiceOrderViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : BaseViewModel() {

    private val serviceOrderFormFieldUIListMLD = MutableLiveData<List<ServiceOrderFormFieldUI>>()
    private val errorMessageMLD = MutableLiveData<String>()
    private val successMessageMLD = MutableLiveData<String>()

    val serviceOrderFormFieldUIListLD: LiveData<List<ServiceOrderFormFieldUI>> = serviceOrderFormFieldUIListMLD
    val errorMessageLD: LiveData<String> = errorMessageMLD
    val successMessageLD: LiveData<String> = successMessageMLD

    lateinit var serviceType: String
    lateinit var serviceName: String

    fun updateArgs(
        serviceType: String,
        serviceName: String
    ) {
        this.serviceType = serviceType
        this.serviceName = serviceName
        fetchData()
    }

    fun fetchData() {
        dataRepository
            .fetchFormForOrderService(type = serviceType)
            .doOnSubscribe { stateLoading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when  (response) {
                        is ResponseEntity.Error -> stateError(response.errorMessage)
                        is ResponseEntity.Hide -> stateError()
                        is ResponseEntity.Success -> {
                            serviceOrderFormFieldUIListMLD.value = response.data.mapToUI()
                            stateSuccess()
                        }
                    }
                },
                onError = { stateError(it.message) }
            ).addTo(compositeDisposable)
    }

    fun orderService(value: String) {
        dataRepository
            .orderService(
                type = serviceType,
                value = value
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { stateLoading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    stateSuccess()
                    when(response) {
                        is ResponseEntity.Hide -> errorMessageMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Error -> errorMessageMLD.value = response.errorMessage
                        is ResponseEntity.Success -> successMessageMLD.value = response.data
                    }
                },
                onError = {
                    stateSuccess()
                    errorMessageMLD.value = it.message ?: "Неизвестная ошибка"
                }
            ).addTo(compositeDisposable)
    }

}