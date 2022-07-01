package com.vodovoz.app.ui.components.fragment.products_catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ProductsCatalogViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val productUIListMLD = MutableLiveData<List<ProductUI>>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val productUIListLD: LiveData<List<ProductUI>> = productUIListMLD

    private val compositeDisposable = CompositeDisposable()

    private lateinit var dataSource: ProductsCatalogFragment.DataSource

    fun updateArgs(dataSource: ProductsCatalogFragment.DataSource) {
        this.dataSource = dataSource
        updateData()
    }

    fun updateData() {
        getProductsByDataSource(dataSource)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            productUIListMLD.value = response.data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    private fun getProductsByDataSource(dataSource: ProductsCatalogFragment.DataSource) = when(dataSource) {
        is ProductsCatalogFragment.DataSource.BannerProducts ->
            dataRepository.fetchProductsByBanner(dataSource.categoryId)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}