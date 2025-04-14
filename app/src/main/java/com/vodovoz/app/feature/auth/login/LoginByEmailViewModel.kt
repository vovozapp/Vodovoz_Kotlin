package com.vodovoz.app.feature.auth.login

import androidx.compose.runtime.Stable
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.agreement.AgreementController
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.common.token.FirebaseTokenManager
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.design_system.model.updateButton
import com.vodovoz.app.domain.general.model.RequestException
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.auth.login.composables.LoginByEmailUiState
import com.vodovoz.app.feature.auth.login.model.LoginByEmailEvent
import com.vodovoz.app.feature.auth.login.model.LoginByEmailState
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.mapToDomain
import com.vodovoz.app.feature.preorder.model.mapToUi
import com.vodovoz.app.feature.preorder.model.updateField
import com.vodovoz.app.feature.preorder.model.updateFieldAndResetErrors
import com.vodovoz.app.feature.sitestate.SiteStateManager
import com.vodovoz.app.ui.mvi.MviViewModel
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Stable
class LoginByEmailViewModel @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val siteStateManager: SiteStateManager,
    private val accountManager: AccountManager,
    private val likeManager: LikeManager,
    private val firebaseTokenManager: FirebaseTokenManager,
    private val resourcesProvider: ResourcesProvider,
) : MviViewModel<LoginByEmailState, LoginByEmailEvent>(LoginByEmailState()) {

    companion object {
        private const val LOGIN_BY_EMAIL_BUTTON = "otpravka"
        private const val NAVIGATION_BUTTON = "registr"
    }

    init {
        fetchLoginByEmailDetails()
    }


    fun navigateBack() = viewModelScope.launch {
        _events.emit(LoginByEmailEvent.GoBack)
    }

    private fun loginByEmail() = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                buttons = s.buttons.updateButton(LOGIN_BY_EMAIL_BUTTON) { btn ->
                    btn.copy(loading = true)
                }
            )
        }


        val fields = stateSnapshot.fields
        val loginByEmailResult =
            vodovozServiceRepository.loginByEmail(stateSnapshot.fields.mapToDomain()).singleResult()


        loginByEmailResult.onSuccess { userAuthInfo ->
            val email = fields.firstOrNull { it.id == "email" }?.value ?: ""
            val password = fields.firstOrNull { it.id == "pass" }?.value ?: ""

            accountManager.updateLastLoginSetting(
                AccountManager.UserSettings(
                    email = email,
                    password = password
                )
            )

            accountManager.updateUserId(userAuthInfo.userId)
            accountManager.updateUserToken(userAuthInfo.token)
            likeManager.updateLikesAfterLogin(userAuthInfo.userId)
            firebaseTokenManager.sendFirebaseToken()

            _state.update { s ->
                s.copy(
                    buttons = s.buttons.updateButton(LOGIN_BY_EMAIL_BUTTON) { btn ->
                        btn.copy(
                            loading = false,
                            enabled = false
                        )
                    }
                )
            }

            _events.emit(LoginByEmailEvent.RefreshAll)

        }.onFailure { t ->
            val defaultMessage = resourcesProvider.getString(R.string.error_login)

            val errorMessage = if (t is RequestException) {
                HtmlCompat.fromHtml(
                    t.errorData?.descriptionHtml ?: defaultMessage,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ).toString()
            } else {
                defaultMessage
            }


            _state.update { s ->

                val lastField = s.fields.lastOrNull()

                s.copy(
                    buttons = s.buttons.updateButton(LOGIN_BY_EMAIL_BUTTON) { btn ->
                        btn.copy(loading = false)
                    },
                    fields = lastField?.let { field ->
                        s.fields.updateField(
                            field,
                            field.copy(supportingText = errorMessage, isError = true)
                        )
                    } ?: s.fields
                )
            }

        }


    }

    fun fetchLoginByEmailDetails() = viewModelScope.launch {
        _state.update { s -> s.copy(uiState = LoginByEmailUiState.Loading) }

        val loginByEmailResult = vodovozServiceRepository.getLoginByEmailDetails().singleResult()

        siteStateManager.requestSiteState()

        loginByEmailResult.onSuccess { loginDetails ->

            if (!siteStateManager.smsEnabled()) {
                _state.update { s ->
                    s.copy(uiState = LoginByEmailUiState.Error)
                }
                return@launch
            }

            val buttons = loginDetails.buttons.map { colorfulButtonModel ->
                colorfulButtonModel.toUi()
            }.updateButton(LOGIN_BY_EMAIL_BUTTON) { it.copy(enabled = false) }

            _state.update { s ->
                s.copy(
                    description = loginDetails.description,
                    title = loginDetails.title,
                    buttons = buttons.updateButton(LOGIN_BY_EMAIL_BUTTON) { btn ->
                        btn.copy(enabled = false)
                    },
                    fields = loginDetails.fields.mapToUi(),
                    uiState = LoginByEmailUiState.Success,
                    agreementHtml = AgreementController.getText(),
                    showAgreement = loginDetails.haveAgreement
                )
            }
        }.onFailure {
            _state.update { s ->
                s.copy(uiState = LoginByEmailUiState.Error)
            }
        }
    }

    fun changeField(field: FieldUi, updatedField: FieldUi) = viewModelScope.launch {
        _state.update { s ->
            val updatedFields = s.fields.updateFieldAndResetErrors(field, updatedField)

            s.copy(
                fields = updatedFields,
                buttons = s.buttons.updateButton(LOGIN_BY_EMAIL_BUTTON) { button ->
                    button.copy(enabled = updatedFields.checkFields() && s.agreementChecked)
                }
            )
        }
    }

    fun activateButton(button: ColorfulButtonUi) = viewModelScope.launch {
        when (button.id) {
            LOGIN_BY_EMAIL_BUTTON -> {
                loginByEmail()
            }

            NAVIGATION_BUTTON -> {
                navigateToRegister()
            }

            else -> {}
        }
    }

    private fun navigateToRegister() = viewModelScope.launch {
        _events.emit(LoginByEmailEvent.GoToRegister)
    }

    fun openAgreementUrl(url: String, titleIndex: Int) = viewModelScope.launch {
        val title = AgreementController.getTitle(titleIndex) ?: ""
        _events.emit(LoginByEmailEvent.GoToWebView(url, title))
    }

    fun checkAgreement(checked: Boolean) = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                agreementChecked = checked,
                buttons = s.buttons.updateButton(LOGIN_BY_EMAIL_BUTTON) { button ->
                    button.copy(enabled = s.fields.checkFields() && checked)
                }
            )
        }
    }

    fun navigateToRecoveryPassword() = viewModelScope.launch {
        _events.emit(LoginByEmailEvent.GoToRecoverPassword)
    }

}