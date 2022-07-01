package com.vodovoz.app.ui.components.fragment.slider.history_slider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.ui.model.HistoryUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class HistorySliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val historyUIListMLD = MutableLiveData<List<HistoryUI>>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val historyUIListLD: LiveData<List<HistoryUI>> = historyUIListMLD

    private val compositeDisposable = CompositeDisposable()

    var historyUIList: List<HistoryUI> = listOf()
        set(value) {
            field = value
            historyUIListMLD.value = value
        }

    fun updateData() {
        dataRepository.fetchHistoriesSlider()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    Log.i(LogSettings.REQ_RES_LOG, "READY HISTORY")
                    when(response) {
                        is ResponseEntity.Success -> {
                            viewStateMLD.value = ViewState.Success()
                            historyUIList = response.data.mapToUI()
                        }
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}