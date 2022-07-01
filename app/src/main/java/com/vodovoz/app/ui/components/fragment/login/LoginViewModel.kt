package com.vodovoz.app.ui.components.fragment.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.ViewState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val loginErrorMLD = MutableLiveData<String>()
    private val emailMLD = MutableLiveData<String>()
    private val passwordMLD = MutableLiveData<String>()
    private val emailInvalidErrorMLD = MutableLiveData<String>()
    private val passwordInvalidErrorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val loginErrorLD: LiveData<String> = loginErrorMLD
    val emailLD: LiveData<String> = emailMLD
    val passwordLD: LiveData<String> = passwordMLD
    val emailInvalidErrorLD: LiveData<String> = emailInvalidErrorMLD
    val passwordInvalidErrorLD: LiveData<String> = passwordInvalidErrorMLD

    private val compositeDisposable = CompositeDisposable()

    init {
        dataRepository.fetchLastLoginData().apply {
            emailMLD.value = email.toString()
            passwordMLD.value = password.toString()
            if (dataRepository.isAlreadyLogin()) {
                email?.let { noNullEmail ->
                    password?.let { noNullPassword ->
                        login(noNullEmail, noNullPassword)
                    }
                }
            }
        }
    }

    fun validate(email: String, password: String) {
        login(email, password)
    }

    private fun login(email: String, password: String) {
        dataRepository
            .login(
                email = email,
                password = password
            )
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Success -> viewStateMLD.value = ViewState.Success()
                        is ResponseEntity.Error -> {
                            loginErrorMLD.value = response.errorMessage
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    fun isAlreadyLogin() = dataRepository.isAlreadyLogin()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}