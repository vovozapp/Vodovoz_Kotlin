package com.vodovoz.app.ui.fragment.orders_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.vodovoz.app.BuildConfig
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.mapper.OrderDetailsMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.ui.model.OrderDetailsUI
import com.vodovoz.app.ui.model.custom.OrdersFiltersBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.map

class OrdersHistoryViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val showCartMLD = MutableLiveData<Boolean>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val showCartLD: LiveData<Boolean> = showCartMLD

    private val compositeDisposable = CompositeDisposable()

    var ordersFiltersBundleUI = OrdersFiltersBundleUI()

    fun updateOrders() = dataRepository
        .fetchAllOrders(
            appVersion = BuildConfig.VERSION_NAME,
            orderId = ordersFiltersBundleUI.orderId,
            status = StringBuilder().apply {
                ordersFiltersBundleUI.orderStatusUIList.forEach { append(it.id).append(",") }
            }.toString()
        ).map { pagingData ->
            pagingData.map { product -> product.mapToUI() }
        }.cachedIn(viewModelScope)

    fun updateFilterBundle(filtersBundle: OrdersFiltersBundleUI) {
        this.ordersFiltersBundleUI = filtersBundle
    }

    fun repeatOrder(orderId: Long) {
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
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка!"
                        is ResponseEntity.Error -> errorMLD.value = response.errorMessage
                        is ResponseEntity.Success -> {
                            repeatOrder(response.data.mapToUI())
                        }
                    }
                },
                onError = { throwable ->
                    viewStateMLD.value = ViewState.Success()
                    errorMLD.value = throwable.message ?: "Неизвестная ошибка!"
                }
            ).addTo(compositeDisposable)

    }

    private fun repeatOrder(orderDetailsUI: OrderDetailsUI) {
        val completableList = mutableListOf<Completable>()
        orderDetailsUI.productUIList.forEach { productUI ->
            completableList.add(dataRepository.changeCart(
                productId = productUI.id,
                quantity = productUI.orderQuantity
            ))
        }

        Completable.merge(completableList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { showCartMLD.value = true },
                onError = { throwable ->
                    viewStateMLD.value = ViewState.Success()
                    errorMLD.value = throwable.message ?: "Неизвестная ошибка!"
                }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}