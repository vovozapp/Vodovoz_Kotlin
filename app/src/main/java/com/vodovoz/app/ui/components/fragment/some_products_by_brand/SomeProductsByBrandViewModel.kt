package com.vodovoz.app.ui.components.fragment.some_products_by_brand

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.mapper.PaginatedProductListMapper.mapToUI
import com.vodovoz.app.ui.model.ProductUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class SomeProductsByBrandViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val productUIListMLD = MutableLiveData<List<ProductUI>>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val productUIListLD: LiveData<List<ProductUI>> = productUIListMLD

    private val compositeDisposable = CompositeDisposable()

    var productId: Long? = null
    var brandId: Long? = null
    var pageIndex: Int = 0
    var pageAmount: Int = Int.MAX_VALUE

    var isUpdatedData = false

    fun updateArgs(productId: Long, brandId: Long) {
        this.productId = productId
        this.brandId = brandId
        nextPage()
    }

    fun nextPage() {
        when(pageIndex) {
            pageAmount -> pageIndex = 1
            else -> ++pageIndex
        }
        updateData()
    }

    fun updateData() {
        dataRepository
            .fetchSomeProductsByBrand(
                productId = productId!!,
                brandId = brandId!!,
                page = pageIndex
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                if (!isUpdatedData) {
                    viewStateMLD.value = ViewState.Loading()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Error -> {
                            pageIndex--
                            if (isUpdatedData) {
                                errorMLD.value = response.errorMessage
                            } else {
                                viewStateMLD.value = ViewState.Error(response.errorMessage)
                            }
                        }
                        is ResponseEntity.Hide -> {
                            pageIndex--
                            if (!isUpdatedData) {
                                viewStateMLD.value = ViewState.Hide()
                            }
                        }
                        is ResponseEntity.Success -> {
                            response.data.mapToUI().let { data ->
                                isUpdatedData = true
                                pageAmount = data.pageAmount
                                productUIListMLD.value = data.productUIList
                                viewStateMLD.value = ViewState.Success()
                            }
                        }
                    }
                },
                onError = {
                    pageIndex--
                    if (isUpdatedData) {
                        errorMLD.value = it.message ?: "Неизвестная ошибка"
                    } else {
                        viewStateMLD.value = ViewState.Error(it.message!!)
                    }

                }
            ).addTo(compositeDisposable)
    }

    fun changeCart(pair: Pair<Long, Int>) {
        dataRepository.changeCart(
            productId = pair.first,
            quantity = pair.second
        ).subscribeBy(
            onComplete = {},
            onError = { throwable ->
                errorMLD.value = throwable.message ?: "Неизвестная ошибка"
            }
        ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}