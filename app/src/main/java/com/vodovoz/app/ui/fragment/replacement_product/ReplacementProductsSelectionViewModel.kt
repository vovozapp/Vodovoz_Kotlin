package com.vodovoz.app.ui.fragment.replacement_product

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ReplacementProductsSelectionViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val errorMLD = SingleLiveEvent<String>()
    val errorLD: LiveData<String> = errorMLD

    private val compositeDisposable = CompositeDisposable()

    var isChangeCart = false

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
            .subscribeBy(
                onComplete = { isChangeCart = true },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" }
            )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}