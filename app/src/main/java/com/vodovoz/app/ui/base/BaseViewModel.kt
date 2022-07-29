package com.vodovoz.app.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {

    protected val viewStateMLD = MutableLiveData<ViewState>()
    val viewStateLD: LiveData<ViewState> = viewStateMLD

    protected val compositeDisposable = CompositeDisposable()

    protected fun stateHide() { viewStateMLD.value = ViewState.Hide() }
    protected fun stateLoading() { viewStateMLD.value = ViewState.Loading() }
    protected fun stateSuccess() { viewStateMLD.value = ViewState.Success() }
    protected fun stateError(errorMessage: String? = null) {
        viewStateMLD.value = ViewState.Error(errorMessage ?: "Неизвестная ошибка")
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}