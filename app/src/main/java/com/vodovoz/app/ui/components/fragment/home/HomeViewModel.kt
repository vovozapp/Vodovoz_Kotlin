package com.vodovoz.app.ui.components.fragment.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.PopupNewsMapper.mapToUI
import com.vodovoz.app.ui.model.PopupNewsUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val popupNewsUIMLD = MutableLiveData<PopupNewsUI>()
    val popupNewsUILD: LiveData<PopupNewsUI> = popupNewsUIMLD

    private val compositeDisposable = CompositeDisposable()

    fun updatePopupNews() {
        dataRepository.fetchPopupNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Success -> popupNewsUIMLD.value = response.data.mapToUI()
                    }
                },
                onError = { throwable ->  Log.i(LogSettings.REQ_RES_LOG, throwable.message!!)}
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}