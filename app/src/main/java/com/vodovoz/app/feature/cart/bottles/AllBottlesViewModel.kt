package com.vodovoz.app.feature.cart.bottles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.BottleMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.BottleUI
import com.vodovoz.app.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllBottlesViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val cartManager: CartManager
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
        viewModelScope.launch {
            runCatching { cartManager.add(bottleId, 0, 1) }
                .onSuccess { addBottleCompletedMLD.value = true }
                .onFailure { errorMLD.value = it.message ?: "Неизвестная ошибка" }
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}