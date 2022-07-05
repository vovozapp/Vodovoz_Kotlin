package com.vodovoz.app.ui.components.fragment.product_detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.ProductDetailBundleMapper.mapToUI
import com.vodovoz.app.ui.model.ProductDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.custom.ProductDetailBundleUI
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class ProductDetailViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val productDetailBundleUIMLD = MutableLiveData<ProductDetailBundleUI>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val productDetailBundleUILD: LiveData<ProductDetailBundleUI> = productDetailBundleUIMLD

    private val compositeDisposable = CompositeDisposable()

    var productId: Long? = null
    var brandId: Long? = null

    lateinit var productDetailBundleUI: ProductDetailBundleUI

    fun updateArgs(productId: Long) {
        Log.i(LogSettings.ID_LOG, "PROD $productId")
        this.productId = productId
        updateProductDetail()
    }

    fun updateProductDetail() {
        dataRepository
            .fetchProductDetails(
                productId = productId!!,
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
                                brandId = data.productDetailUI.brandUI?.id
                                productDetailBundleUI = data
                                productDetailBundleUIMLD.value = data
                                viewStateMLD.value = ViewState.Success()
                            }
                        }
                    }
                },
                onError = { viewStateMLD.value = ViewState.Error(it.message!!) }
            ).addTo(compositeDisposable)
    }

    fun changeCart() {
        dataRepository.changeCart(
            productId = productDetailBundleUI.productDetailUI.id,
            quantity = productDetailBundleUI.productDetailUI.cartQuantity
        ).subscribeBy(
            onComplete = {},
            onError = { throwable ->
                errorMLD.value = throwable.message ?: "Неизвестная ошибка"
            }
        ).addTo(compositeDisposable)
    }

    fun isLogin() = dataRepository.isAlreadyLogin()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}