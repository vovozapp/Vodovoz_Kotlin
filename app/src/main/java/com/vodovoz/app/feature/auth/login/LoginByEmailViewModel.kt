package com.vodovoz.app.feature.auth.login

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.design_system.model.updateButton
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.auth.login.composables.LoginByEmailUiState
import com.vodovoz.app.feature.auth.login.model.LoginByEmailEvent
import com.vodovoz.app.feature.auth.login.model.LoginByEmailState
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.mapToUi
import com.vodovoz.app.feature.preorder.model.updateFieldAndResetErrors
import com.vodovoz.app.ui.mvi.MviViewModel
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginByEmailViewModel @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
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

    fun fetchLoginByEmailDetails() = viewModelScope.launch {
        _state.update { s -> s.copy(uiState = LoginByEmailUiState.Loading) }

        val loginByEmailResult = vodovozServiceRepository.getLoginByEmailDetails().singleResult()

        loginByEmailResult.onSuccess { loginDetails ->

            val buttons = loginDetails.buttons.map { colorfulButtonModel ->
                colorfulButtonModel.toUi()
            }.updateButton(LOGIN_BY_EMAIL_BUTTON) { it.copy(enabled = false) }

            _state.update { s ->
                s.copy(
                    description = loginDetails.description,
                    title = loginDetails.title,
                    buttons = buttons,
                    fields = loginDetails.fields.mapToUi(),
                    uiState = LoginByEmailUiState.Success
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
                    button.copy(enabled = updatedFields.checkFields())
                }
            )
        }
    }

    fun activateButton(button: ColorfulButtonUi) = viewModelScope.launch {
        when (button.id) {
            LOGIN_BY_EMAIL_BUTTON -> {

            }

            NAVIGATION_BUTTON -> {

            }

            else -> {}
        }
    }

}