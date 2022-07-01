package com.vodovoz.app.ui.components.fragment.slider.popular_slider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class PopularSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val categoryUIListMLD = MutableLiveData<List<CategoryUI>>()
    private val viewStateMLD = MutableLiveData<ViewState>()

    val categoryUIListLD: LiveData<List<CategoryUI>> = categoryUIListMLD
    val viewStateLD: LiveData<ViewState> = viewStateMLD

    private val compositeDisposable = CompositeDisposable()

    fun updateData() {
        dataRepository.fetchPopularSlider()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    Log.i(LogSettings.REQ_RES_LOG, "READY POPULAR")
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            viewStateMLD.value = ViewState.Success()
                            categoryUIListMLD.value = response.data.mapToUI().sortedBy { it.id }
                        }
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