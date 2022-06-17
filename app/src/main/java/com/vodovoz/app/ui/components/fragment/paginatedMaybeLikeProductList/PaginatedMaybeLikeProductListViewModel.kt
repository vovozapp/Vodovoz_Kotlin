package com.vodovoz.app.ui.components.fragment.paginatedMaybeLikeProductList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.PaginatedProductListMapper.mapToUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class PaginatedMaybeLikeProductListViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<List<ProductUI>>>()
    val fetchStateLD: LiveData<FetchState<List<ProductUI>>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    var pageIndex: Int = 0
    var pageAmount: Int = Int.MAX_VALUE

    init { nextPage() }

    fun nextPage() {
        when(pageIndex) {
            pageAmount -> pageIndex = 1
            else -> ++pageIndex
        }
        updateData()
    }

    fun updateData() = dataRepository
        .fetchPaginatedMaybeLikeProductList(
            pageIndex = pageIndex
        )
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        response.data?.let {
                            val data = response.data.mapToUI()
                            pageAmount = data.pageAmount
                            fetchStateMLD.value = FetchState.Success(data.productUIList)
                        }
                    }
                    is ResponseEntity.Error -> {
                        fetchStateMLD.value = FetchState.Hide()
                    }
                }
            },
            onError = { fetchStateMLD.value = FetchState.Error(it.message) }
        ).addTo(compositeDisposable)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}