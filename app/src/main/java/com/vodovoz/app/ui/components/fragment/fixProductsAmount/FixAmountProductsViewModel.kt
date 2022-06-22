package com.vodovoz.app.ui.components.fragment.fixProductsAmount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.PromotionUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class FixAmountProductsViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<List<ProductUI>>>()
    val fetchStateLD: LiveData<FetchState<List<ProductUI>>> = fetchStateMLD

    private val compositeDisposable = CompositeDisposable()

    private lateinit var dataSource: FixAmountProductsFragment.DataSource

    fun updateArgs(dataSource: FixAmountProductsFragment.DataSource) {
        this.dataSource = dataSource
        updateData()
    }

    fun updateData() {
        getProductsByDataSource(dataSource)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response -> fetchStateMLD.value = FetchState.Success(response.data?.mapToUI())},
                onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message) }
            ).addTo(compositeDisposable)
    }

    private fun getProductsByDataSource(dataSource: FixAmountProductsFragment.DataSource) = when(dataSource) {
        is FixAmountProductsFragment.DataSource.BannerProducts ->
            dataRepository.fetchProductsByBanner(dataSource.categoryId)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}