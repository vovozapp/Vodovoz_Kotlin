package com.vodovoz.app.ui.components.fragment.slider.comment_slider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.CommentMapper.mapToUI
import com.vodovoz.app.ui.model.CommentUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class CommentSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val commentUIListMLD = MutableLiveData<List<CommentUI>>()
    private val viewStateMLD = MutableLiveData<ViewState>()

    val commentUIListLD: LiveData<List<CommentUI>> = commentUIListMLD
    val viewStateLD: LiveData<ViewState> = viewStateMLD

    private val compositeDisposable = CompositeDisposable()

    fun updateData() {
        dataRepository.fetchCommentsSlider()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    Log.i(LogSettings.REQ_RES_LOG, "READY COMMENT")
                    when(response) {
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Success -> {
                            viewStateMLD.value = ViewState.Success()
                            commentUIListMLD.value = response.data.mapToUI()
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