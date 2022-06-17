package com.vodovoz.app.ui.components.fragment.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.FetchState
import com.vodovoz.app.ui.mapper.LastLoginDataMapper.mapToEntity
import com.vodovoz.app.ui.model.LastLoginDataUI
import com.vodovoz.app.ui.model.custom.ConcreteFilterBundleUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.log

class LoginViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<Boolean>>()
    private val loginErrorMLD = MutableLiveData<String>()
    private val emailMLD = MutableLiveData<String>()
    private val passwordMLD = MutableLiveData<String>()
    private val emailInvalidErrorMLD = MutableLiveData<String>()
    private val passwordInvalidErrorMLD = MutableLiveData<String>()

    val fetchStateLD: LiveData<FetchState<Boolean>> = fetchStateMLD
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

    fun login(email: String, password: String) = dataRepository
        .login(
            email = email,
            password = password
        )
        .subscribeOn(Schedulers.io())
        .doOnSubscribe { fetchStateMLD.value = FetchState.Loading() }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
        onSuccess = { response ->
             when(response) {
                 is ResponseEntity.Success -> fetchStateMLD.value = FetchState.Success(true)
                 is ResponseEntity.Error -> {
                     loginErrorMLD.value = response.errorMessage!!
                     fetchStateMLD.value = FetchState.Success(false)
                 }
             }
        },
        onError = { throwable -> fetchStateMLD.value = FetchState.Error(throwable.message) }
    )

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun isAlreadyLogin() = dataRepository.isAlreadyLogin()

}