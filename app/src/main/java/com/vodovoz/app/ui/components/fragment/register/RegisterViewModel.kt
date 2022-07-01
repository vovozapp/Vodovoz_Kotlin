package com.vodovoz.app.ui.components.fragment.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.config.AuthConfig.EMAIL_IS_ALREADY_REGISTERED
import com.vodovoz.app.data.config.FieldValidateConfig.EMAIL_REGEX
import com.vodovoz.app.data.config.FieldValidateConfig.FIRST_NAME_LENGTH
import com.vodovoz.app.data.config.FieldValidateConfig.PASSWORD_LENGTH
import com.vodovoz.app.data.config.FieldValidateConfig.PHONE_REGEX
import com.vodovoz.app.data.config.FieldValidateConfig.SECOND_NAME_LENGTH
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class RegisterViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val firstNameErrorMLD = MutableLiveData<String>()
    private val secondNameErrorMLD = MutableLiveData<String>()
    private val emailErrorMLD = MutableLiveData<String>()
    private val passwordErrorMLD = MutableLiveData<String>()
    private val phoneErrorMLD = MutableLiveData<String>()
    private val isRegisterSuccessMLD = MutableLiveData<Boolean>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val firstNameErrorLD: LiveData<String> = firstNameErrorMLD
    val secondNameErrorLD: LiveData<String> = secondNameErrorMLD
    val emailErrorLD: LiveData<String> = emailErrorMLD
    val passwordErrorLD: LiveData<String> = passwordErrorMLD
    val phoneErrorLD: LiveData<String> = phoneErrorMLD
    val isRegisterSuccessLD: LiveData<Boolean> = isRegisterSuccessMLD

    private val compositeDisposable = CompositeDisposable()

    private var isFirstTry = true

    var firstName = ""
        set(value) {
            field = value
            validateFirstName()
        }
    var secondName = ""
        set(value) {
            field = value
            validateSecondName()
        }
    var phone = ""
        set(value) {
            field = value
            validatePhone()
        }
    var email = ""
        set(value) {
            field = value
            validateEmail()
        }
    var password = ""
        set(value) {
            field = value
            validatePassword()
        }

    fun validate() {
        isFirstTry = false
        var isValid = true

        if (!validateFirstName()) isValid = false
        if (!validateSecondName()) isValid = false
        if (!validateEmail()) isValid = false
        if (!validatePhone()) isValid = false
        if (!validatePassword()) isValid = false

        if (isValid) register()
    }

    private fun register() {
        dataRepository
            .register(
                firstName = firstName,
                secondName = secondName,
                password = password,
                email = email,
                phone = phone
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> {
                            when(response.errorMessage) {
                                EMAIL_IS_ALREADY_REGISTERED -> emailErrorMLD.value = EMAIL_IS_ALREADY_REGISTERED
                            }
                            viewStateMLD.value = ViewState.Success()
                        }
                        is ResponseEntity.Success -> {
                            isRegisterSuccessMLD.value = true
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    private fun validateFirstName() = FIRST_NAME_LENGTH.contains(firstName.length).apply {
        firstNameErrorMLD.value =
            if (!this && !isFirstTry) "Длина $FIRST_NAME_LENGTH"
            else ""
    }

    private fun validateSecondName() = SECOND_NAME_LENGTH.contains(secondName.length).apply {
        secondNameErrorMLD.value =
            if (!this && !isFirstTry) "Длина $SECOND_NAME_LENGTH"
            else ""
    }

    private fun validatePassword() = PASSWORD_LENGTH.contains(password.length).apply {
        passwordErrorMLD.value =
            if (!this && !isFirstTry) "Длина $PASSWORD_LENGTH"
            else ""
    }

    private fun validatePhone() = PHONE_REGEX.matches(phone).apply {
        phoneErrorMLD.value =
            if (!this && !isFirstTry) "Некорректная номер"
            else ""
    }

    private fun validateEmail() = EMAIL_REGEX.matches(email).apply {
        emailErrorMLD.value =
            if (!this && !isFirstTry) "Некорректная почта"
            else ""
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}