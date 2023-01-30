package com.vodovoz.app.ui.fragment.login

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.enum.AuthType
import com.vodovoz.app.util.LogSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val loginErrorMLD = MutableLiveData<String>()
    private val emailMLD = MutableLiveData<String>()
    private val passwordMLD = MutableLiveData<String>()
    private val emailInvalidErrorMLD = MutableLiveData<String>()
    private val passwordInvalidErrorMLD = MutableLiveData<String>()
    private val dialogMessageMLD = MutableLiveData<String>()
    private val errorMLD = MutableLiveData<String>()
    private val requestCodeCompletedMLD = MutableLiveData<Boolean>()
    private val countDownTimerFinishedMLD = MutableLiveData<Boolean>()
    private val countDownTimerTickMLD = MutableLiveData<Int>()
    private val codeInvalidErrorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val loginErrorLD: LiveData<String> = loginErrorMLD
    val emailLD: LiveData<String> = emailMLD
    val passwordLD: LiveData<String> = passwordMLD
    val emailInvalidErrorLD: LiveData<String> = emailInvalidErrorMLD
    val passwordInvalidErrorLD: LiveData<String> = passwordInvalidErrorMLD
    val dialogMessageLD: LiveData<String> = dialogMessageMLD
    val errorLD: LiveData<String> = errorMLD
    val requestCodeCompletedLD: LiveData<Boolean> = requestCodeCompletedMLD
    val countDownTimerFinishedLD: LiveData<Boolean> = countDownTimerFinishedMLD
    val countDownTimerTickLD: LiveData<Int> = countDownTimerTickMLD
    val codeInvalidErrorLD: LiveData<String> = codeInvalidErrorMLD

    private val compositeDisposable = CompositeDisposable()

    var trackErrorsForAuthByPhone: Boolean = false
    var authType: AuthType = AuthType.PHONE

    var codeTimeOutCountDownTimer: CountDownTimer? = null
    var timerIsCancel = true

    init {
        dataRepository.fetchLastLoginData().apply {
            emailMLD.value = email.toString()
            passwordMLD.value = password.toString()
            if (dataRepository.isAlreadyLogin()) {
                email?.let { noNullEmail ->
                    password?.let { noNullPassword ->
                        authByEmail(noNullEmail, noNullPassword)
                    }
                }
            }
        }
    }

    fun validateAuthEmail(email: String, password: String) {
        authByEmail(email, password)
    }

    private fun authByEmail(email: String, password: String) {
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

    fun requestCode(phone: String) {
        dataRepository
            .requestCode(phone)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Error -> errorMLD.value = it.errorMessage
                        is ResponseEntity.Hide -> errorMLD.value = "Неизвестная ошибка"
                        is ResponseEntity.Success -> {
                            requestCodeCompletedMLD.value = true
                            startCountDownTimer(it.data)
                        }
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" }
            ).addTo(compositeDisposable)
    }

    fun authByPhone(phone: String, code: String) {
        dataRepository
            .authByPhone(phone, code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    when(it) {
                        is ResponseEntity.Error -> codeInvalidErrorMLD.value = it.errorMessage
                        is ResponseEntity.Hide -> codeInvalidErrorMLD.value = "Неверный код"
                        is ResponseEntity.Success -> viewStateMLD.value = ViewState.Success()
                    }
                },
                onError = { errorMLD.value = it.message ?: "Неизвестная ошибка"}
            ).addTo(compositeDisposable)
    }

    fun startCountDownTimer(seconds: Int) {
        Log.d(LogSettings.LOCAL_DATA, "START = $seconds")
        codeTimeOutCountDownTimer?.cancel()
        codeTimeOutCountDownTimer = object: CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownTimerTickMLD.value = (millisUntilFinished/1000).toInt()
            }
            override fun onFinish() {
                countDownTimerFinishedMLD.value = true
                timerIsCancel = true
            }
        }
        timerIsCancel = false
        codeTimeOutCountDownTimer?.start()
    }

    fun fetchLastRequestCodeDate() = dataRepository.fetchLastRequestCodeDate()
    fun fetchLastRequestCodeTimeOut() = dataRepository.fetchLastRequestCodeTimeOut()
    fun fetchLastAuthPhone() = dataRepository.fetchLastAuthPhone()

    fun isAlreadyLogin() = dataRepository.isAlreadyLogin()

    fun recoverPassword(email: String) {
        if (email.length < 4) {
            passwordInvalidErrorMLD.value = "Неправильно указан email"
            return
        }

        dataRepository
            .recoverPassword(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { dialogMessageMLD.value = "Пароль упешно изменен и выслан вам на почту: $email"},
                onError = { throwable ->  errorMLD.value = throwable.message ?: "Неизвестная ошибка"}
            ).addTo(compositeDisposable)
    }

    fun clearData() {
        dataRepository.updateLastRequestCodeDate(0)
        dataRepository.updateLastRequestCodeTimeOut(0)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}