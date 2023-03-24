package com.vodovoz.app.feature.catalog

import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.stringToErrorState
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.ui.model.CategoryUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : PagingStateViewModel<CatalogViewModel.CatalogState>(CatalogState()) {

    private val compositeDisposable = CompositeDisposable()

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            updateData()
        }
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(loadingPage = true)
        updateData()
    }

    private fun updateData() {
        dataRepository.fetchCatalog()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> {}
                        is ResponseEntity.Error -> state.copy(error = response.errorMessage.stringToErrorState(), loadingPage = false)
                        is ResponseEntity.Success -> {
                            uiStateListener.value = state.copy(
                                loadingPage = false,
                                data = state.data.copy(itemsList = response.data.mapToUI()),
                                error = null
                            )
                        }
                    }
                },
                onError = { throwable -> uiStateListener.value =
                    state.copy(error = throwable.toErrorState(), loadingPage = false)
                }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    data class CatalogState(
        val itemsList: List<CategoryUI> = emptyList()
    ) : State
}