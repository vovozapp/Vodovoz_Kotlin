package com.vodovoz.app.feature.auth.login

import android.os.CountDownTimer
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.account.data.LoginManager
import com.vodovoz.app.common.agreement.AgreementController
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.token.FirebaseTokenManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.mapToUi
import com.vodovoz.app.feature.preorder.model.updateFieldAndResetErrors
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.ui.model.enum.AuthType
import com.vodovoz.app.util.FieldValidationsSettings
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LoginFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val firebaseTokenManager: FirebaseTokenManager,
    private val loginManager: LoginManager,
    private val siteStateManager: SiteStateManager,
    private val likeManager: LikeManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<LoginFlowViewModel.LoginState, LoginFlowViewModel.LoginEvents>(
    LoginState()
) {

    init {
        viewModelScope.launch {
            siteStateManager
                .observeSiteState()
                .collect { siteState ->
                    if (siteState != null) {
                        uiStateListener.updateData { s ->
                            //TODO - change siteState.requestUrl to new state
                            s.copy(requestUrl = siteState.requestUrl, showRegisterText = siteState.requestUrl == null)
                        }
                    } else {
                        siteStateManager.requestSiteState()
                        delay(2000L)
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

        fetchLoginDetails()
    }

    private var codeTimeOutCountDownTimer: CountDownTimer? = null
    private var timerIsCancel = true

    fun fetchLoginDetails() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(uiState = LoginUiState.Loading)
        }

        val loginDetailsResult = vodovozServiceRepository.getLoginDetails().singleResult()

        loginDetailsResult.onSuccess { loginDetails ->
            uiStateListener.updateData { s ->
                s.copy(
                    description = loginDetails.description,
                    title = loginDetails.title,
                    navigationButton = loginDetails.navigationButton.toUi(),
                    mainButton = loginDetails.mainButton.toUi(),
                    fields = loginDetails.fields.mapToUi(),
                    agreementTextHtml = AgreementController.getText(),
                    uiState = LoginUiState.Success,
                    showAgreements = loginDetails.hasAgreement,
                    mainButtonEnabled = false,
                    mainButtonLoading = false,
                )
            }
        }.onFailure {
            uiStateListener.updateData { s ->
                s.copy(uiState = LoginUiState.Error)
            }
        }
    }

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

        vodovozServiceRepository.loginByEmail(email, password).onEach { result ->
            result.onSuccess { message ->
                debugLog { message }
            }.onFailure { throwable ->
                debugLog { throwable.stackTraceToString() }
            }
        }.launchIn(viewModelScope)

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
                            accountManager.updateUserId(response.data.id)
                            accountManager.updateUserToken(response.data.token)

                            likeManager.updateLikesAfterLogin(response.data.id)

                            uiStateListener.value =
                                state.copy(error = null, loadingPage = false)
                            clearData()
                            eventListener.emit(LoginEvents.AuthSuccess)
                            firebaseTokenManager.sendFirebaseToken()
                        }

                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(MessageType.Message(response.errorMessage)))
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

    fun authByPhone(phone: String, code: String): Job {
        //TODO - show toast if requestUrl is empty
        val url = state.data.requestUrl ?: return viewModelScope.launch { }
        uiStateListener.value = state.copy(loadingPage = true)
        return viewModelScope.launch {
            flow { emit(repository.authByPhone(phone = phone, url = url, code = code)) }.take(1)
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(MessageType.WrongCode))
                        }

                        is ResponseEntity.Success -> {
                            accountManager.updateUserId(response.data.id)
                            accountManager.updateUserToken(response.data.token)
                            likeManager.updateLikesAfterLogin(response.data.id)
                            loginManager.updateLastAuthPhone(phone)

                            uiStateListener.value =
                                state.copy(error = null, loadingPage = false)
                            clearData()
                            eventListener.emit(LoginEvents.AuthSuccess)
                            firebaseTokenManager.sendFirebaseToken()
                        }

                        is ResponseEntity.Error -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(MessageType.Message(response.errorMessage)))
                        }
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "auth by phone error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }.firstOrNull()
        }
    }

    fun requestCode(phone: String) {
        clearData()
        val url = state.data.requestUrl ?: return
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow {
                emit(repository.requestCode(phone = phone, url = url))
            }
                .onEach { response ->
                    when (response) {
                        is ResponseEntity.Hide -> {
                            uiStateListener.value =
                                state.copy(loadingPage = false)
                            eventListener.emit(LoginEvents.AuthError(MessageType.WrongCode))
                        }

                        //995 898 76 82

                        is ResponseEntity.Success -> {
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
                            eventListener.emit(LoginEvents.AuthError(MessageType.Message(response.errorMessage)))
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
        if (!FieldValidationsSettings.EMAIL_REGEX.matches(email)) {
            viewModelScope.launch {
                eventListener.emit(LoginEvents.PasswordRecoverError(MessageType.WrongEmail))
            }
            return
        }
        uiStateListener.value = state.copy(loadingPage = true)
        viewModelScope.launch {
            flow { emit(repository.recoverPassword(email)) }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value =
                            state.copy(error = null, loadingPage = false)
                        eventListener.emit(
                            LoginEvents.PasswordRecoverSuccess(
                                MessageType.Message(
                                    email
                                )
                            )
                        )
                    } else {
                        uiStateListener.value =
                            state.copy(loadingPage = false)
                        eventListener.emit(LoginEvents.PasswordRecoverError(MessageType.RepeatError))
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
        loginManager.updateLastRequestCodeDate(0)
        loginManager.updateLastRequestCodeTimeOut(0)
    }

    fun checkIfLoginAlready() {
        accountManager.fetchAccountId() ?: return
        viewModelScope.launch {
            eventListener.emit(LoginEvents.AuthSuccess)
        }
    }

    fun showPassword() {
        uiStateListener.value =
            state.copy(data = state.data.copy(showPassword = !state.data.showPassword))
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(LoginEvents.GoBack)
    }

    fun changeField(field: FieldUi, updatedField: FieldUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val updatedFields = s.fields.updateFieldAndResetErrors(field, updatedField)
            s.copy(fields = updatedFields, mainButtonEnabled = updatedFields.checkFields() && s.agreementChecked)
        }
    }

    fun openAgreementUrl(url: String, index: Int) = viewModelScope.launch {
        val title = AgreementController.getTitle(index) ?: ""
        eventListener.emit(LoginEvents.GoToWebView(url, title))
    }

    fun checkSubscribe(checked: Boolean) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                subscribeChecked = checked
            )
        }
    }

    fun checkAgreement(checked: Boolean) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                agreementChecked = checked,
                mainButtonEnabled = s.fields.checkFields() && checked
            )
        }

    }

    fun navigateToLoginByEmail() = viewModelScope.launch {
        eventListener.emit(LoginEvents.GoToLoginByEmail)
    }

    sealed class LoginEvents : Event {
        data object AuthSuccess : LoginEvents()
        data class AuthError(val message: MessageType) : LoginEvents()
        data class TimerTick(val tick: Int) : LoginEvents()
        data object TimerFinished : LoginEvents()
        data object CodeComplete : LoginEvents()
        data class PasswordRecoverSuccess(val message: MessageType) : LoginEvents()
        data class PasswordRecoverError(val message: MessageType) : LoginEvents()

        data object AuthByPhone : LoginEvents()
        data object AuthByEmail : LoginEvents()
        data object GoBack : LoginEvents()
        data object GoToLoginByEmail : LoginEvents()

        data class SetupByPhone(val time: Int, val phone: String) : LoginEvents()
        data class GoToWebView(val url: String, val title: String) : LoginEvents()
    }

    sealed interface MessageType {
        data object WrongEmail : MessageType
        data object RepeatError : MessageType
        data object WrongCode : MessageType
        data class Message(val param: String) : MessageType
    }

    @Immutable
    data class LoginState(
        val authType: AuthType = AuthType.PHONE,
        val requestUrl: String? = null,
        val settings: AccountManager.UserSettings? = null,
        val lastAuthPhone: String? = null,
        val showPassword: Boolean = false,


        val showRegisterText: Boolean = false,
        val agreementTextHtml: String = "",
        val showAgreements: Boolean = false,
        val agreementChecked: Boolean = true,
        val subscribeChecked: Boolean = false,
        val title: String = "",
        val description: String = "",
        val fields: List<FieldUi> = emptyList(),
        val mainButton: ColorfulButtonUi = ColorfulButtonUi.Empty,
        val mainButtonEnabled: Boolean = false,
        val mainButtonLoading: Boolean = false,
        val navigationButton: ColorfulButtonUi = ColorfulButtonUi.Empty,
        val uiState: LoginUiState = LoginUiState.Loading,
    ) : State

    sealed interface LoginUiState {
        data object Success : LoginUiState
        data object Loading : LoginUiState
        data object Error : LoginUiState
    }
}