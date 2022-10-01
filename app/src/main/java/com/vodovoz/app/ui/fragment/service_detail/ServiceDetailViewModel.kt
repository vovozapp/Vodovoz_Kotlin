package com.vodovoz.app.ui.fragment.service_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.ServiceMapper.mapToUI
import com.vodovoz.app.ui.base.BaseViewModel
import com.vodovoz.app.ui.model.ServiceUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ServiceDetailViewModel(
    private val dataRepository: DataRepository
) : BaseViewModel() {

    private val serviceUIMLD = MutableLiveData<ServiceUI>()
    val serviceUILD: LiveData<ServiceUI> = serviceUIMLD

    private lateinit var serviceTypeList: List<String>
    lateinit var serviceUIList: List<ServiceUI>
    lateinit var selectedServiceType: String

    fun updateArgs(
        serviceTypeList: List<String>,
        selectedServiceType: String
    ) {
        this.serviceTypeList = serviceTypeList
        this.selectedServiceType = selectedServiceType
        fetchServices()
    }

    fun fetchServices() {
        Observable.fromIterable(serviceTypeList)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { stateLoading() }
            .flatMap { dataRepository.fetchServiceById(it).toObservable() }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { responseList ->
                    val serviceUIList = mutableListOf<ServiceUI>()
                    for (response in responseList) {
                        when(response) {
                            is ResponseEntity.Error -> {
                                stateError(response.errorMessage)
                                return@subscribeBy
                            }
                            is ResponseEntity.Hide -> {
                                stateError("Неизвестная ошибка")
                                return@subscribeBy
                            }
                            is ResponseEntity.Success -> serviceUIList.add(response.data.mapToUI())
                        }
                        this.serviceUIList = serviceUIList
                        serviceUIMLD.value = serviceUIList.find { it.type == selectedServiceType } ?: serviceUIList.first()
                        stateSuccess()
                    }
                },
                onError = { stateError(it.message ?: "Неизвестная ошибка") }
            ).addTo(compositeDisposable)
    }

    fun selectService(type: String) {
        this.selectedServiceType = type
        serviceUIMLD.value = serviceUIList.find { it.type == type } ?: serviceUIList.first()
    }


}