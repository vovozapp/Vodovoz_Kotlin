package com.vodovoz.app.ui.fragment.promotion_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.PromotionDetailMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.PromotionDetailUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PromotionDetailsViewModel @Inject constructor(
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
    var promotionDetailUI: PromotionDetailUI? = null

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
                            promotionDetailUI = response.data.mapToUI()
                            promotionDetailUIMLD.value = promotionDetailUI
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        when(isFavorite) {
            true -> dataRepository.addToFavorite(productId)
            false -> dataRepository.removeFromFavorite(productId = productId)

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {},
                onError = { throwable -> errorMLD.value = throwable.message ?: "Неизвестная ошибка" }
            ).addTo(compositeDisposable)
    }

    fun changeCart(productId: Long, quantity: Int) {
        dataRepository
            .changeCart(productId, quantity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun isAlreadyLogin() = dataRepository.isAlreadyLogin()

}