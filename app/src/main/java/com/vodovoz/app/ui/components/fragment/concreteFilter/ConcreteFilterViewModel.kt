package com.vodovoz.app.ui.components.fragment.concreteFilter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.FilterValueMapper.mapToUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.custom.ConcreteFilterBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ConcreteFilterViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<ConcreteFilterBundleUI>>()
    val fetchStateLD: LiveData<FetchState<ConcreteFilterBundleUI>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    var filter: FilterUI? = null
    var categoryId: Long? = null

    fun setArgs(filter: FilterUI, categoryId: Long) {
        this.filter = filter
        this.categoryId = categoryId
        updateData()
    }

    fun updateData() = dataRepository
        .fetchConcreteFilterResponse(
            categoryId = categoryId!!,
            filterCode = filter!!.code
        )
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                response.data?.let {
                    val data = it.mapToUI()
                    validateData(data)
                    fetchStateMLD.value = FetchState.Success(
                        ConcreteFilterBundleUI(
                            filterUI = filter!!,
                            filterValueList = data
                        )
                    )
                }
            },
            onError = { fetchStateMLD.value = FetchState.Error(it.message)}
        ).addTo(compositeDisposable)

    private fun validateData(filterValueList: List<FilterValueUI>) {
        filter?.filterValueList?.forEach { customFilterValue ->
            filterValueList.find { it.id == customFilterValue.id }?.let {
                it.isSelected = customFilterValue.isSelected
            }
        }
    }

    fun prepareFilter(filterValueList: List<FilterValueUI>) {
        filter?.let { noNullFilter ->
            noNullFilter.filterValueList.clear()
            filterValueList.forEach { filterValue ->
                if (filterValue.isSelected) {
                    noNullFilter.filterValueList.add(filterValue)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}