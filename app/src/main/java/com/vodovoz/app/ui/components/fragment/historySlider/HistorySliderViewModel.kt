package com.vodovoz.app.ui.components.fragment.historySlider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.ui.model.HistoryUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class HistorySliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val historyListMLD = MutableLiveData<List<HistoryUI>>()
    private val stateHideMLD = MutableLiveData<Boolean>()

    val historyListLD: LiveData<List<HistoryUI>> = historyListMLD
    val sateHideLD: LiveData<Boolean> = stateHideMLD

    private val compositeDisposable = CompositeDisposable()

    init {
        dataRepository.historySubject
            .subscribeBy { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        response.data?.let { noNullData ->
                            historyListMLD.value = noNullData.mapToUI()
                        }
                    }
                    is ResponseEntity.Error -> {
                        stateHideMLD.value = true
                    }
                }
            }
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}