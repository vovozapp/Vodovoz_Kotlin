package com.vodovoz.app.ui.components.fragment.allBrands

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.BrandEntity
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.BrandMapper.mapToUI
import com.vodovoz.app.ui.model.BrandUI
import com.vodovoz.app.ui.model.custom.FilterBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AllBrandsViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<List<BrandUI>>>()
    val fetchStateLD: LiveData<FetchState<List<BrandUI>>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    private lateinit var brandIdList: List<Long>

    fun updateArgs(brandIdList: List<Long>) {
        this.brandIdList = brandIdList
        updateData()
    }

    fun updateData() { dataRepository
        .fetchAllBrands(brandIdList = brandIdList)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response -> fetchStateMLD.value = FetchState.Success(response.data?.mapToUI()) },
            onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message) }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}