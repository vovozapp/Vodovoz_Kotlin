package com.vodovoz.app.ui.components.fragment.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.ProductDetailBundleMapper.mapToUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.custom.ProductDetailBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ProductDetailViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<ProductDetailBundleUI>>()
    private val brandProductListMLD = MutableLiveData<List<ProductUI>>()

    val fetchStateLD: LiveData<FetchState<ProductDetailBundleUI>> = fetchStateMLD
    val brandProductListLD: LiveData<List<ProductUI>> = brandProductListMLD

    private val compositeDisposable = CompositeDisposable()

    var productId: Long? = null
    var brandId: Long? = null

    fun setArgs(productId: Long) {
        this.productId = productId
        updateProductDetail()
    }

    fun updateProductDetail() = dataRepository
        .fetchProductDetail(
            productId = productId!!,
        )
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { response ->
                response.data?.let {
                    val data = response.data.mapToUI()
                    brandId = data.productDetailUI.brandUI!!.id
                    fetchStateMLD.value = FetchState.Success(data)
                }
            },
            onError = { fetchStateMLD.value = FetchState.Error(it.message) }
        ).addTo(compositeDisposable)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}