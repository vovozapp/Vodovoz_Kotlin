package com.vodovoz.app.ui.components.fragment.promotion_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.mapper.PromotionDetailMapper.mapToUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.PromotionDetailUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class PromotionDetailViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val promotionDetailUIMLD = MutableLiveData<PromotionDetailUI>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val promotionDetailUILD: LiveData<PromotionDetailUI> = promotionDetailUIMLD

    private val compositeDisposable = CompositeDisposable()

    var promotionId: Long? = null

    fun updateArgs(promotionId: Long) {
        this.promotionId = promotionId
        updateData()
    }

    fun updateData() {
        dataRepository.fetchPromotionDetails(promotionId = promotionId!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            promotionDetailUIMLD.value = response.data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    fun changeCart(productUI: ProductUI) {
        dataRepository.changeCart(
            productId = productUI.id,
            quantity = productUI.cartQuantity
        ).subscribeOn(
            Schedulers.io()
        ).observeOn(
            AndroidSchedulers.mainThread()
        ).subscribeBy(
            onComplete = {},
            onError = { throwable ->  errorMLD.value = throwable.message }
        ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}