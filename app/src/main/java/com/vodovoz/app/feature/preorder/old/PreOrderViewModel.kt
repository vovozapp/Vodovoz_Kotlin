package com.vodovoz.app.feature.preorder.old

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.PreOrderFormDataMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.PreOrderFormDataUI
import com.vodovoz.app.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class PreOrderViewModel @Inject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    val successPreOrderMessageSLD = SingleLiveEvent<String>()

    private val preOrderFormDataUIMLD = MutableLiveData<PreOrderFormDataUI>()
    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()

    val preOrderFormDataUILD: LiveData<PreOrderFormDataUI> = preOrderFormDataUIMLD
    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD

    private val compositeDisposable = CompositeDisposable()

    private var productId: Long? = null

    fun setupArgs(productId: Long) {
        this.productId = productId
    }

    var trackValidationErrors = false

    fun fetchPreOrderFormData() {
        dataRepository.fetchPreOrderFormData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(it.errorMessage)
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Неизвестная ошибка!")
                        is ResponseEntity.Success -> {
                            preOrderFormDataUIMLD.value = it.data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { viewStateMLD.value = ViewState.Error(it.message ?: "Неизвестная ошибка!") }
            ).addTo(compositeDisposable)
    }

    fun preOrderProduct(
        name: String,
        email: String,
        phone: String
    ) {
        dataRepository
            .preOrderProduct(
                productId = productId,
                name = name,
                email = email,
                phone = phone
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Error -> errorMLD.value = it.errorMessage
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка!"
                        is ResponseEntity.Success -> {
                            successPreOrderMessageSLD.value = it.data.toString()
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка!" }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}