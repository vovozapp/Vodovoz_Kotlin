package com.vodovoz.app.ui.fragment.order_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.mapper.OrderDetailsMapper.mapToUI
import com.vodovoz.app.ui.model.OrderDetailsUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class OrderDetailsViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val orderDetailsUIMLD = MutableLiveData<OrderDetailsUI>()
    private val errorMLD = MutableLiveData<String>()
    private val showCartMLD = MutableLiveData<Boolean>()
    private val messageMLD = MutableLiveData<String>()
    private val cancelOrderMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val orderDetailsUILD: LiveData<OrderDetailsUI> = orderDetailsUIMLD
    val errorLD: LiveData<String> = errorMLD
    val showCartLD: LiveData<Boolean> = showCartMLD
    val messageLD: LiveData<String> = messageMLD
    val cancelOrderLD: LiveData<String> = cancelOrderMLD

    private val compositeDisposable = CompositeDisposable()

    var orderId: Long = 0
    lateinit var orderDetailsUI: OrderDetailsUI

    fun updateArgs(orderId: Long) {
        this.orderId = orderId
        updateData()
    }

    fun updateData() {
        dataRepository
            .fetchOrderDetails(
                orderId = orderId,
                appVersion = BuildConfig.VERSION_NAME
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                     when(response) {
                         is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Неизвестная ошибка!")
                         is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                         is ResponseEntity.Success -> {
                             orderDetailsUI = response.data.mapToUI()
                             orderDetailsUIMLD.value = orderDetailsUI
                             viewStateMLD.value = ViewState.Success()
                         }
                     }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка!") }
            ).addTo(compositeDisposable)
    }

    fun repeatOrder() {
        val completableList = mutableListOf<Completable>()
        orderDetailsUI.productUIList.forEach { productUI ->
            completableList.add(dataRepository.changeCart(
                productId = productUI.id,
                quantity = productUI.orderQuantity
            ))
        }

        Completable.merge(completableList)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { showCartMLD.value = true },
                onError = { throwable ->
                    viewStateMLD.value = ViewState.Success()
                    errorMLD.value = throwable.message ?: "Неизвестная ошибка!"
                }
            ).addTo(compositeDisposable)
    }

    fun cancelOrder() {
        dataRepository
            .cancelOrder(orderId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Error -> errorMLD.value = it.errorMessage
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка!"
                        is ResponseEntity.Success -> {
                            cancelOrderMLD.value = it.data.toString()
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка!"}
            )
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
        compositeDisposable.clear()
    }

}