package com.vodovoz.app.ui.fragment.full_screen_history_slider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.HistoryMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.HistoryUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class FullScreenHistoriesSliderViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val historyUIListMLD = MutableLiveData<List<HistoryUI>>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val historyUIListLD: LiveData<List<HistoryUI>> = historyUIListMLD

    private val compositeDisposable = CompositeDisposable()

    var historyUIList = listOf<HistoryUI>()

    fun updateData() {
        dataRepository.fetchHistoriesSlider()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when (response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            historyUIList = response.data.mapToUI()
                            historyUIListMLD.value = historyUIList
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable ->
                    viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка")
                }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}