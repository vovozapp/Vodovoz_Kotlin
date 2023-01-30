package com.vodovoz.app.ui.fragment.some_products_maybe_like

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.PaginatedProductListMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.ProductUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SomeProductsMaybeLikeViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val productUIListMLD = MutableLiveData<List<ProductUI>>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val productUIListLD: LiveData<List<ProductUI>> = productUIListMLD

    private val compositeDisposable = CompositeDisposable()

    var pageIndex: Int = 0
    var pageAmount: Int = Int.MAX_VALUE

    fun nextPage() {
        when(pageIndex) {
            pageAmount -> pageIndex = 1
            else -> ++pageIndex
        }
        updateData()
    }

    var isUpdatedData = false

    fun updateData() {
        dataRepository
            .fetchMaybeLikeProducts(page = pageIndex)
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
                                viewStateMLD.value = ViewState.Hide()
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

}