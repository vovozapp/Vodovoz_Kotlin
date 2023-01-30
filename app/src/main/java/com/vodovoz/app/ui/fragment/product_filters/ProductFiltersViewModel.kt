package com.vodovoz.app.ui.fragment.product_filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.FilterBundleMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ProductFiltersViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val filtersBundleUIMLD = MutableLiveData<FiltersBundleUI>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val filtersBundleUILD: LiveData<FiltersBundleUI> = filtersBundleUIMLD

    private val compositeDisposable = CompositeDisposable()

    var customFilterBundle: FiltersBundleUI? = null
    var categoryId: Long? = null
    var defaultFilterBundle: FiltersBundleUI? = null

    fun setArgs(filterBundle: FiltersBundleUI, categoryId: Long) {
        this.categoryId = categoryId
        this.customFilterBundle = filterBundle
        updateData()
    }

    fun updateData() {
        dataRepository
            .fetchAllFiltersByCategory(categoryId!!)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            defaultFilterBundle = response.data.mapToUI()
                            mergeFiltersBundles()
                            filtersBundleUIMLD.value = defaultFilterBundle
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { viewStateMLD.value = ViewState.Error(it.message!!)}
            ).addTo(compositeDisposable)
    }

    private fun mergeFiltersBundles() {
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
                    -1 -> {
                        noNullCustomFilterBundle.filterUIList.add(concreteFilter)
                    }
                    else -> {
                        noNullCustomFilterBundle.filterUIList[index] = concreteFilter
                    }
                }
            }
            mergeFiltersBundles()
            filtersBundleUIMLD.value = defaultFilterBundle
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
        compositeDisposable.dispose()
    }

}