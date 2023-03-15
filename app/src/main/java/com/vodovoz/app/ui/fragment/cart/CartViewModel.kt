package com.vodovoz.app.ui.fragment.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.CartBundleMapper.mapUoUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.custom.CartBundleUI
import com.vodovoz.app.util.SingleLiveEvent
import com.vodovoz.app.util.calculatePrice
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val availableProductListMLD = MutableLiveData<List<ProductUI>>()
    private val notAvailableProductListMLD = MutableLiveData<List<ProductUI>>()
    private val giftProductListMLD = MutableLiveData<List<ProductUI>>()
    private val bestForYouCategoryDetailMLD = MutableLiveData<CategoryDetailUI>()
    private val giftMessageMLD = MutableLiveData<String?>()
    private val errorMLD = SingleLiveEvent<String>()
    private val fullPriceMLD = MutableLiveData<Int>()
    private val depositPriceMLD = MutableLiveData<Int>()
    private val discountPriceMLD = MutableLiveData<Int>()
    private val totalPriceMLD = MutableLiveData<Int>()
    private val infoMessageMLD = SingleLiveEvent<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val availableProductListLD: LiveData<List<ProductUI>> = availableProductListMLD
    val notAvailableProductListLD: LiveData<List<ProductUI>> = notAvailableProductListMLD
    val giftProductListLD: LiveData<List<ProductUI>> = giftProductListMLD
    val bestForYouCategoryDetailLD: LiveData<CategoryDetailUI> = bestForYouCategoryDetailMLD
    val giftMessageLD: LiveData<String?> = giftMessageMLD
    val errorLD: LiveData<String> = errorMLD
    val fullPriceLD: LiveData<Int> = fullPriceMLD
    val depositPriceLD: LiveData<Int> = depositPriceMLD
    val discountPriceLD: LiveData<Int> = discountPriceMLD
    val totalPriceLD: LiveData<Int> = totalPriceMLD
    val infoMessageLD: LiveData<String> = infoMessageMLD

    private val compositeDisposable = CompositeDisposable()

    var needUpdateCart: Boolean = false

    var coupon: String = ""

    var full: Int = 0
        set(value) {
            field = value
            fullPriceMLD.value = field
        }
    var deposit: Int = 0
        set(value) {
            field = value
            depositPriceMLD.value = field
        }
    var discount: Int = 0
        set(value) {
            field = value
            discountPriceMLD.value = field
        }
    var total: Int = 0
        set(value) {
            field = value
            totalPriceMLD.value = field
        }

    private var giftProductUIList = listOf<ProductUI>()
        set(value) {
            field = value
            giftProductListMLD.value = value
        }

    fun getGiftList() = giftProductUIList

    var notAvailableProductUIList = listOf<ProductUI>()
    var availableProductUIList = listOf<ProductUI>()
        set(value) {
            field = value
            availableProductListMLD.value = value
            val prices = calculatePrice(availableProductUIList)
            full = prices.fullPrice
            discount = prices.discountPrice
            deposit = prices.deposit
            total = prices.fullPrice + prices.deposit - prices.discountPrice
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

    fun isAlreadyLogin() = dataRepository.isAlreadyLogin()

    fun getCart(): String {
        val cart = availableProductUIList.map { Pair(it.id, it.cartQuantity) }
        val result = StringBuilder()
        for (product in cart) {
            result.append(product.first).append(":").append(product.second).append(",")
        }
        return result.toString()
    }

    fun updateData() {
        dataRepository
            .fetchCart(coupon)
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
        infoMessageMLD.value = cartBundleUI.infoMessage
        if (cartBundleUI.infoMessage.isNotEmpty()) {
            coupon = ""
        }
        giftProductUIList = cartBundleUI.giftProductUIList
        availableProductUIList = cartBundleUI.availableProductUIList

        giftMessageMLD.value = cartBundleUI.giftMessage
        notAvailableProductUIList = cartBundleUI.notAvailableProductUIList
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
        dataRepository
            .changeCart(
                productId = productId,
                quantity = quantity
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
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
        compositeDisposable.dispose()
    }

}