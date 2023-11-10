package com.vodovoz.app.feature.auth.login

import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.account.data.LoginManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.token.TokenManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.ui.model.enum.AuthType
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
    private val localDataSource: LocalDataSource,
    private val accountManager: AccountManager,
    private val tokenManager: TokenManager,
    private val loginManager: LoginManager,
    private val siteStateManager: SiteStateManager,
    private val likeManager: LikeManager,
) : PagingContractViewModel<LoginFlowViewModel.LoginState, LoginFlowViewModel.LoginEvents>(
    LoginState()
) {

    init {
        viewModelScope.launch {
            siteStateManager
                .observeSiteState()
                .collect {
                    if (it != null) {
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                requestUrl = it.requestPhone
                            )
                        )
                    } else {
                        siteStateManager.requestSiteState()
                    }
                }

        }

        viewModelScope.launch {
            val settings = accountManager.fetchUserSettings()
            val phone = loginManager.fetchLastAuthPhone()
            viewModelScope.launch {
                uiStateListener.value = state.copy(
                    data = state.data.copy(
                        settings = settings,
                        lastAuthPhone = phone
                    )
                )
            }
        }
    }

    private var codeTimeOutCountDownTimer: CountDownTimer? = null
    private var timerIsCancel = true

    fun signIn() {
        viewModelScope.launch {
            when (state.data.authType) {
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
        uiStateListener.value = state.copy(
            loadingPage = true, data = state.data.copy(
                settings = AccountManager.UserSettings(
                    email = email,
                    password = password
                )
            )
        )
        viewModelScope.launch {
            flow { emit(repository.authByEmail(email, password)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {}
                        is ResponseEntity.Success -> {
                            accountManager.updateLastLoginSetting(
                                AccountManager.UserSettings(
                                    email = email,
                                    password = password
                                )
                            )
                            accountManager.updateUserId(response.data)
                            likeManager.updateLikesAfterLogin(response.data)
                            localDataSource.updateUserId(response.data)

                            uiStateListener.value =
                                state.copy(error = null, loadingPage = false)
                            clearData()
                            eventListener.emit(LoginEvents.AuthSuccess)
                            tokenManager.sendFirebaseToken()
                        }
                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(response.errorMessage))
                        }
                    }

                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "auth by email error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun authByPhone(phone: String, code: String) {
        val url = state.data.requestUrl ?: return
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.authByPhone(phone = phone, url = url, code = code)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError("Неверный код"))
                        }
                        is ResponseEntity.Success -> {
                            accountManager.updateUserId(response.data)
                            likeManager.updateLikesAfterLogin(response.data)
                            loginManager.updateLastAuthPhone(phone)
                            localDataSource.updateUserId(response.data)

                            uiStateListener.value =
                                state.copy(error = null, loadingPage = false)
                            clearData()
                            eventListener.emit(LoginEvents.AuthSuccess)
                            tokenManager.sendFirebaseToken()
                        }
                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(response.errorMessage))
                        }
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "auth by phone error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun requestCode(phone: String) {
        val url = state.data.requestUrl ?: return
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.requestCode(phone = phone, url = url)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    when (response) {
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
                .catch {
                    debugLog { "request code error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun startCountDownTimer(seconds: Int) {
        viewModelScope.launch {
            codeTimeOutCountDownTimer?.cancel()
            codeTimeOutCountDownTimer = object : CountDownTimer(seconds * 1000L, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    viewModelScope.launch {
                        eventListener.emit(LoginEvents.TimerTick((millisUntilFinished / 1000).toInt()))
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
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.recoverPassword(email)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
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
                .catch {
                    debugLog { "recover password error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
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
        val expiredTime =
            ((curTime - loginManager.fetchLastRequestCodeDate() - loginManager.fetchLastRequestCodeTimeOut() * 1000) / 1000).toInt()
        val phone = loginManager.fetchLastAuthPhone()
        viewModelScope.launch {
            eventListener.emit(LoginEvents.SetupByPhone(expiredTime, phone))
        }
    }

    private fun clearData() {
        localDataSource.updateLastRequestCodeDate(0)
        localDataSource.updateLastRequestCodeTimeOut(0)

        loginManager.updateLastRequestCodeDate(0)
        loginManager.updateLastRequestCodeTimeOut(0)
    }

    fun checkIfLoginAlready() {
        accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            eventListener.emit(LoginEvents.AuthSuccess)
        }
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
        val authType: AuthType = AuthType.PHONE,
        val requestUrl: String? = null,
        val settings: AccountManager.UserSettings? = null,
        val lastAuthPhone: String? = null,
    ) : State
}