package com.vodovoz.app.ui.components.fragment.concrete_filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
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

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val concreteFilterBundleUIMLD = MutableLiveData<ConcreteFilterBundleUI>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val concreteFilterBundleUILD: LiveData<ConcreteFilterBundleUI> = concreteFilterBundleUIMLD

    private val compositeDisposable = CompositeDisposable()

    var filter: FilterUI? = null
    var categoryId: Long? = null

    fun updateArgs(filter: FilterUI, categoryId: Long) {
        this.filter = filter
        this.categoryId = categoryId
        updateData()
    }

    fun updateData() = dataRepository
        .fetchProductFilterById(
            categoryId = categoryId!!,
            filterCode = filter!!.code
        )
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                when(response) {
                    is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                    is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                    is ResponseEntity.Success -> {
                        response.data.mapToUI().let { data ->
                            validateData(data)
                            concreteFilterBundleUIMLD.value = ConcreteFilterBundleUI(
                                filterUI = filter!!,
                                filterValueList = data
                            )
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                }
            },
            onError = { viewStateMLD.value = ViewState.Error(it.message!!)}
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