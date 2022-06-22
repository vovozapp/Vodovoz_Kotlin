package com.vodovoz.app.ui.components.fragment.allProductFilters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.FilterBundleMapper.mapToUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.custom.FilterBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class FiltersViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<FilterBundleUI>>()
    val fetchStateLD: LiveData<FetchState<FilterBundleUI>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    var customFilterBundle: FilterBundleUI? = null
    var defaultFilterBundle: FilterBundleUI? = null
    var categoryId: Long? = null

    fun setArgs(filterBundle: FilterBundleUI, categoryId: Long) {
        this.categoryId = categoryId
        this.customFilterBundle = filterBundle
        updateData()
    }

    fun updateData() = dataRepository
        .fetchAllFiltersByCategory(categoryId!!)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                response.data?.let {
                    defaultFilterBundle = response.data.mapToUI()
                    validateData()
                    fetchStateMLD.value = FetchState.Success(defaultFilterBundle)
                }
            },
            onError = { fetchStateMLD.value = FetchState.Error(it.message)}
        ).addTo(compositeDisposable)

    private fun validateData() {
        customFilterBundle?.let { noNullCustomFilterBundle ->
            defaultFilterBundle?.let { noNullDefaultFilterBundle ->
                noNullCustomFilterBundle.filterUIList.forEach { customFilter ->
                    noNullDefaultFilterBundle.filterUIList.find { filter ->
                        filter.code == customFilter.code
                    }?.filterValueList = customFilter.filterValueList
                }
            }
        }
    }

    fun removeConcreteFilter(concreteFilter: FilterUI) {
        customFilterBundle?.filterUIList?.removeAll { it.code == concreteFilter.code }
    }

    fun changeConcreteFilter(concreteFilter: FilterUI) {
        if (concreteFilter.filterValueList.isNotEmpty()) {
            customFilterBundle?.let { noNullCustomFilterBundle ->
                when(val index = noNullCustomFilterBundle.filterUIList.indexOfFirst { it.code == concreteFilter.code }) {
                    -1 -> noNullCustomFilterBundle.filterUIList.add(concreteFilter)
                    else -> noNullCustomFilterBundle.filterUIList[index] = concreteFilter
                }
                validateData()
                fetchStateMLD.value = FetchState.Success(defaultFilterBundle)
            }
        }
    }

    fun clearCustomFilterBundle() {
        customFilterBundle?.let { noNullFilterBundle ->
            noNullFilterBundle.filterPriceUI.maxPrice = Int.MAX_VALUE
            noNullFilterBundle.filterPriceUI.minPrice = Int.MIN_VALUE
            noNullFilterBundle.filterUIList.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}