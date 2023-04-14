package com.vodovoz.app.feature.auth.login

import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.account.data.LoginManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.user.AuthByPhoneJsonParser.parseAuthByPhoneResponse
import com.vodovoz.app.data.parser.response.user.LoginResponseJsonParser.parseLoginResponse
import com.vodovoz.app.data.parser.response.user.RecoverPasswordJsonParser.parseRecoverPasswordResponse
import com.vodovoz.app.data.parser.response.user.RequestCodeResponseJsonParser.parseRequestCodeResponse
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.ui.model.enum.AuthType
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tabManager: TabManager,
    private val localDataSource: LocalDataSource,
    private val accountManager: AccountManager,
    private val loginManager: LoginManager
) : PagingContractViewModel<LoginFlowViewModel.LoginState, LoginFlowViewModel.LoginEvents>(
    LoginState()
) {

    private var codeTimeOutCountDownTimer: CountDownTimer? = null
    private var timerIsCancel = true

    fun signIn() {
        viewModelScope.launch {
            when(state.data.authType) {
                AuthType.EMAIL -> {
                    eventListener.emit(LoginEvents.AuthByEmail)
                }
                AuthType.PHONE -> {
                    eventListener.emit(LoginEvents.AuthByPhone)
                }
            }
        }
    }

    fun authByEmail(email: String, password: String) {
        viewModelScope.launch {
            flow { emit(repository.authByEmail(email, password)) }
                .catch {
                    debugLog { "auth by email error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    when(val response = it.parseLoginResponse()) {
                        is ResponseEntity.Hide -> {}
                        is ResponseEntity.Success -> {
                            accountManager.updateLastLoginSetting(
                                AccountManager.UserSettings(
                                    email = email,
                                    password = password
                                )
                            )
                            accountManager.updateUserId(response.data)
                            localDataSource.updateUserId(response.data)

                            uiStateListener.value =
                                state.copy(error = null, loadingPage = false)
                            clearData()
                            eventListener.emit(LoginEvents.AuthSuccess)
                        }
                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(response.errorMessage))
                        }
                    }

                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun authByPhone(phone: String, code: String) {
        viewModelScope.launch {
            flow { emit(repository.authByPhone(phone, code)) }
                .catch {
                    debugLog { "auth by phone error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    when(val response = it.parseAuthByPhoneResponse()) {
                        is ResponseEntity.Hide -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError("Неверный код"))
                        }
                        is ResponseEntity.Success -> {
                            accountManager.updateUserId(response.data)
                            loginManager.updateLastAuthPhone(phone)
                            localDataSource.updateUserId(response.data)

                            uiStateListener.value =
                                state.copy(error = null, loadingPage = false)
                            clearData()
                            eventListener.emit(LoginEvents.AuthSuccess)
                        }
                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(response.errorMessage))
                        }
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun requestCode(phone: String) {
        viewModelScope.launch {
            flow { emit(repository.requestCode(phone)) }
                .catch {
                    debugLog { "request code error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    when(val response = it.parseRequestCodeResponse()) {
                        is ResponseEntity.Hide -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError("Неверный код"))
                        }
                        is ResponseEntity.Success -> {
                            localDataSource.updateLastRequestCodeDate(Date().time)
                            localDataSource.updateLastAuthPhone(phone)
                            localDataSource.updateLastRequestCodeTimeOut(response.data)

                            loginManager.updateLastRequestCodeDate(Date().time)
                            loginManager.updateLastAuthPhone(phone)
                            loginManager.updateLastRequestCodeTimeOut(response.data)

                            uiStateListener.value =
                                state.copy(error = null, loadingPage = false)
                            eventListener.emit(LoginEvents.CodeComplete)
                            
                            startCountDownTimer(response.data)
                        }
                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(response.errorMessage))
                        }
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun startCountDownTimer(seconds: Int) {
        viewModelScope.launch {
            codeTimeOutCountDownTimer?.cancel()
            codeTimeOutCountDownTimer = object: CountDownTimer(seconds * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    viewModelScope.launch {
                        eventListener.emit(LoginEvents.TimerTick((millisUntilFinished/1000).toInt()))
                    }
                }
                override fun onFinish() {
                    viewModelScope.launch {
                        eventListener.emit(LoginEvents.TimerFinished)
                    }
                    timerIsCancel = true
                }
            }
            timerIsCancel = false
            codeTimeOutCountDownTimer?.start()
        }
    }
    
    fun recoverPassword(email: String) {

        if (email.length < 4) {
            viewModelScope.launch { 
                eventListener.emit(LoginEvents.PasswordRecoverError("Неправильно указан email"))
            }
            return
        }
        
        viewModelScope.launch {
            flow { emit(repository.recoverPassword(email)) }
                .catch {
                    debugLog { "recover password error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                   val response = it.parseRecoverPasswordResponse()
                   if (response is ResponseEntity.Success) {
                       uiStateListener.value =
                           state.copy(error = null, loadingPage = false)
                       eventListener.emit(LoginEvents.PasswordRecoverSuccess("Пароль упешно изменен и выслан вам на почту: $email"))
                   } else {
                       uiStateListener.value =
                           state.copy(loadingPage = false)
                       eventListener.emit(LoginEvents.PasswordRecoverError("Ошибка. Попробуйте снова."))
                   }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun changeAuthType() {
        if (state.data.authType == AuthType.PHONE) {
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    authType = AuthType.EMAIL
                )
            )
        } else {
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    authType = AuthType.PHONE
                )
            )
        }
    }

    fun setupByPhone() {
        val curTime = Date().time
        val expiredTime = ((curTime - loginManager.fetchLastRequestCodeDate() - loginManager.fetchLastRequestCodeTimeOut()*1000)/1000).toInt()
        val phone = loginManager.fetchLastAuthPhone()
        viewModelScope.launch {
            eventListener.emit(LoginEvents.SetupByPhone(expiredTime, phone))
        }
    }

    fun clearData() {
        localDataSource.updateLastRequestCodeDate(0)
        localDataSource.updateLastRequestCodeTimeOut(0)

        loginManager.updateLastRequestCodeDate(0)
        loginManager.updateLastRequestCodeTimeOut(0)
    }

    sealed class LoginEvents : Event {
        object AuthSuccess : LoginEvents()
        data class AuthError(val message: String) : LoginEvents()
        data class TimerTick(val tick: Int) : LoginEvents()
        object TimerFinished : LoginEvents()
        object CodeComplete : LoginEvents()
        data class PasswordRecoverSuccess(val message: String) : LoginEvents()
        data class PasswordRecoverError(val message: String) : LoginEvents()

        object AuthByPhone : LoginEvents()
        object AuthByEmail : LoginEvents()

        data class SetupByPhone(val time: Int, val phone: String) : LoginEvents()
    }

    data class LoginState(
        val authType: AuthType = AuthType.PHONE
    ) : State
}