package com.vodovoz.app.ui.components.fragment.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class CatalogViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<List<CategoryUI>>>()
    val fetchStateLD: LiveData<FetchState<List<CategoryUI>>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    init { updateData() }

    var lastFetchState: FetchState<List<CategoryUI>>? = null
        set(value) {
            field = value
            fetchStateMLD.value = field
        }

    fun updateData() = dataRepository.updateCatalog()
        .doOnSuccess { lastFetchState = FetchState.Loading() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                response.data?.let {
                    lastFetchState = FetchState.Success(it.mapToUI())
                }
            },
            onError = { throwable -> lastFetchState = FetchState.Error(throwable.message!!)}
        ).addTo(compositeDisposable)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}