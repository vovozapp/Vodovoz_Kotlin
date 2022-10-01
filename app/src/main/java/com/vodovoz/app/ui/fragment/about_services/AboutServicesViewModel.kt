package com.vodovoz.app.ui.fragment.about_services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.AboutServicesBundleMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.custom.AboutServicesBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AboutServicesViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val aboutServicesBundleMLD = MutableLiveData<AboutServicesBundleUI>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val aboutServicesBundleLD: LiveData<AboutServicesBundleUI> = aboutServicesBundleMLD

    private val compositeDisposable = CompositeDisposable()

    lateinit var serviceTypeList: List<String>

    fun updateData() {
        dataRepository
            .fetchAboutServices()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                     when(response) {
                         is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                         is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                         is ResponseEntity.Success -> {
                             val data = response.data.mapToUI()
                             aboutServicesBundleMLD.value = data
                             serviceTypeList = data.serviceUIList.map { it.type }
                             viewStateMLD.value = ViewState.Success()
                         }
                     }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка") }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}