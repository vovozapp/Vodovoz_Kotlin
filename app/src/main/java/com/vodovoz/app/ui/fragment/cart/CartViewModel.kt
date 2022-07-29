package com.vodovoz.app.ui.fragment.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.mapper.CartBundleMapper.mapUoUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.custom.CartBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class CartViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val availableProductListMLD = MutableLiveData<List<ProductUI>>()
    private val notAvailableProductListMLD = MutableLiveData<List<ProductUI>>()
    private val giftProductListMLD = MutableLiveData<List<ProductUI>>()
    private val bestForYouCategoryDetailMLD = MutableLiveData<CategoryDetailUI>()
    private val giftMessageMLD = MutableLiveData<String?>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val availableProductListLD: LiveData<List<ProductUI>> = availableProductListMLD
    val notAvailableProductListLD: LiveData<List<ProductUI>> = notAvailableProductListMLD
    val giftProductListLD: LiveData<List<ProductUI>> = giftProductListMLD
    val bestForYouCategoryDetailLD: LiveData<CategoryDetailUI> = bestForYouCategoryDetailMLD
    val giftMessageLD: LiveData<String?> = giftMessageMLD
    val errorLD: LiveData<String> = errorMLD

    private val compositeDisposable = CompositeDisposable()

    private var giftProductUIList = listOf<ProductUI>()
        set(value) {
            field = value
            giftProductListMLD.value = value
        }

    fun getGiftList() = giftProductUIList

    private var availableProductUIList = listOf<ProductUI>()
        set(value) {
            field = value
            availableProductListMLD.value = value
        }

    var isTryToClearCart = false
    var isFirstUpdate = true

    private fun updateViewState(viewState: ViewState) {
        if (isFirstUpdate) {
            viewStateMLD.value = viewState
            if (viewState is ViewState.Success) {
                isFirstUpdate = false
            }
        }
    }

    fun updateData() {
        dataRepository.fetchCart()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { updateViewState(ViewState.Loading()) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> updateViewState(ViewState.Hide())
                        is ResponseEntity.Error -> updateViewState(ViewState.Error(response.errorMessage))
                        is ResponseEntity.Success -> {
                            response.data.let { data ->
                                updateLiveData(data.mapUoUI())
                                updateViewState(ViewState.Success())
                            }
                        }
                    }
                },
                onError = { throwable ->
                    if (!isFirstUpdate) errorMLD.value = throwable.message
                    updateViewState(ViewState.Error(throwable.message!!))
                }
            ).addTo(compositeDisposable)
    }

    private fun updateLiveData(cartBundleUI: CartBundleUI) {
        giftProductUIList = cartBundleUI.giftProductUIList
        availableProductUIList = cartBundleUI.availableProductUIList

        giftMessageMLD.value = cartBundleUI.giftMessage
        notAvailableProductListMLD.value = cartBundleUI.notAvailableProductUIList

        cartBundleUI.bestForYouCategoryDetailUI?.let { bestForYouCategoryDetailMLD.value = it }
    }

    fun clearCart() {
        dataRepository.clearCart()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                isFirstUpdate = true
                updateViewState(ViewState.Loading())
                isTryToClearCart = true
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    isTryToClearCart = false
                    updateData()
                },
                onError = { throwable ->  viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка") }
            ).addTo(compositeDisposable)
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        when(isFavorite) {
            true -> dataRepository.addToFavorite(productId)
            false -> dataRepository.removeFromFavorite(productId = productId)

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    errorMLD.value = "Добавил"
                },
                onError = { throwable -> errorMLD.value = throwable.message ?: "Неизвестная ошибка" }
            ).addTo(compositeDisposable)
    }

    fun changeCart(productId: Long, quantity: Int) {
        dataRepository.changeCart(
            productId = productId,
            quantity = quantity
        ).subscribeBy(
            onComplete = {
                updateData()
                updateData()
            },
            onError = { throwable ->
                errorMLD.value = throwable.message ?: "Неизвестная ошибка"
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}