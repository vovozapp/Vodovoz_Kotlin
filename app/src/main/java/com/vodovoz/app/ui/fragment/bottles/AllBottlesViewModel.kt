package com.vodovoz.app.ui.fragment.bottles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.BottleMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.ui.model.BottleUI
import com.vodovoz.app.ui.model.BrandUI
import com.vodovoz.app.util.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AllBottlesViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val bottleUIListMLD = MutableLiveData<List<BottleUI>>()
    private val errorMLD = SingleLiveEvent<String>()
    private val addBottleCompletedMLD = SingleLiveEvent<Boolean>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val bottleUIListLD: LiveData<List<BottleUI>> = bottleUIListMLD
    val errorLD: LiveData<String> = errorMLD
    val addBottleCompletedLD: LiveData<Boolean> = addBottleCompletedMLD

    private val compositeDisposable = CompositeDisposable()

    fun updateData() {
        dataRepository
            .fetchBottles()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Success -> {
                            bottleUIListMLD.value = response.data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    fun addBottleToCart(bottleId: Long) {
        dataRepository
            .changeCart(
                productId = bottleId,
                quantity = 1
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = { addBottleCompletedMLD.value = true },
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