package com.vodovoz.app.ui.fragment.discount_card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.ActivateDiscountCardBundleMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.custom.ActivateDiscountCardBundleUI
import com.vodovoz.app.util.FieldValidationsSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class DiscountCardViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val activateDiscountCardBundleUIMLD = MutableLiveData<ActivateDiscountCardBundleUI>()
    private val messageMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val activateDiscountCardBundleUILD: LiveData<ActivateDiscountCardBundleUI> = activateDiscountCardBundleUIMLD
    val messageLD: LiveData<String> = messageMLD

    private val compositeDisposable = CompositeDisposable()

    lateinit var activateDiscountCardBundleUI: ActivateDiscountCardBundleUI

    fun fetchData() {
        dataRepository
            .fetchActivateDiscountCardInfo()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Hide")
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                        is ResponseEntity.Success -> {
                            activateDiscountCardBundleUI = response.data.mapToUI()
                            activateDiscountCardBundleUIMLD.value = activateDiscountCardBundleUI
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message ?: "Неизвестная ошибка!") }
            ).addTo(compositeDisposable)
    }

    fun activateDiscountCard() {
        val valueBuilder = StringBuilder()
        var isCorrectly = true
        for (property in activateDiscountCardBundleUI.discountCardPropertyUIList) {
            when(property.code) {
                "TELEFON" -> if (!FieldValidationsSettings.PHONE_REGEX.matches(property.value)) {
                    isCorrectly = false
                    property.isValid = false
                }
                else -> if (property.value.isEmpty()) {
                    isCorrectly = false
                    property.isValid = false
                }
            }
            valueBuilder
                .append(property.code)
                .append("$")
                .append(property.value)
                .append(";")
        }

        if (!isCorrectly) {
            activateDiscountCardBundleUIMLD.value = activateDiscountCardBundleUI
            return
        }

        dataRepository
            .activateDiscountCard(value = valueBuilder.toString())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    viewStateMLD.value = ViewState.Success()
                    when(response) {
                        is ResponseEntity.Hide -> messageMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Error -> messageMLD.value = response.errorMessage
                        is ResponseEntity.Success -> messageMLD.value = response.data.toString()
                    }
                },
                onError = { throwable ->
                    viewStateMLD.value = ViewState.Success()
                    messageMLD.value = throwable.message ?: "Неизвестная ошибка!"
                }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}