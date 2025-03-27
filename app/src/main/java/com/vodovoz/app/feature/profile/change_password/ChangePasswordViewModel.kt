package com.vodovoz.app.feature.profile.change_password

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.R
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.domain.general.model.UserNotLoginException
import com.vodovoz.app.domain.general.model.ValidationException
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.preorder.model.FieldUi
import com.vodovoz.app.feature.preorder.model.checkFields
import com.vodovoz.app.feature.preorder.model.mapToDomain
import com.vodovoz.app.feature.preorder.model.toUi
import com.vodovoz.app.feature.preorder.model.updateFieldAndResetErrors
import com.vodovoz.app.feature.preorder.model.updateFieldValueAndResetErrors
import com.vodovoz.app.feature.profile.change_password.model.ChangePasswordEvent
import com.vodovoz.app.feature.profile.change_password.model.ChangePasswordState
import com.vodovoz.app.feature.profile.change_password.model.ChangePasswordUiState
import com.vodovoz.app.ui.mvi.MviViewModel
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val resourcesProvider: ResourcesProvider,
) : MviViewModel<ChangePasswordState, ChangePasswordEvent>(
    ChangePasswordState()
) {
    init {
        fetchChangePasswordDetails()
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(ChangePasswordEvent.GoBack)
    }


    private fun fetchChangePasswordDetails() = viewModelScope.launch {
        _state.update { s ->
            s.copy(uiState = ChangePasswordUiState.Loading)
        }
        val changePasswordDetailsResult =
            vodovozServiceRepository.getChangePasswordDetails().singleResult()
        changePasswordDetailsResult.onSuccess { model ->
            _state.update { s ->
                s.copy(
                    fields = model.fields.map { field -> field.toUi() },
                    title = model.title,
                    uiState = ChangePasswordUiState.ChangePassword
                )
            }
        }.onFailure { t ->
            when (t) {
                is UserNotLoginException -> {
                    _events.emit(ChangePasswordEvent.Logout)
                }

                else -> {
                    _events.emit(ChangePasswordEvent.GoBack)
                }
            }
        }
    }

    fun changeFieldValue(field: FieldUi, newValue: String) = viewModelScope.launch {
        _state.update { s ->
            val updatedFields = s.fields.updateFieldValueAndResetErrors(field, newValue)
            s.copy(
                fields = updatedFields,
                buttonEnabled = updatedFields.checkFields()
            )
        }
    }

    fun updatePassword() = viewModelScope.launch {

        val fields = stateSnapshot.fields.mapToDomain()
        val passwordField = fields.firstOrNull() ?: return@launch

        _state.update { s ->
            s.copy(buttonLoading = true)
        }

        val updatePasswordResult =
            vodovozServiceRepository.updatePassword(passwordField.value).singleResult()
        updatePasswordResult.onSuccess {
            _state.update { s ->
                s.copy(uiState = ChangePasswordUiState.PasswordChanged)
            }
        }.onFailure { t ->
            when (t) {
                is UserNotLoginException -> {
                    _events.emit(ChangePasswordEvent.Logout)
                }

                is ValidationException -> {
                    _events.emit(
                        ChangePasswordEvent.ShowSnackbar(
                            t.message
                                ?: resourcesProvider.getString(R.string.error_password_unknown)
                        )
                    )
                }

                else -> {
                    _events.emit(
                        ChangePasswordEvent.ShowSnackbar(resourcesProvider.getString(R.string.error_password_unknown))
                    )
                }
            }
        }
        _state.update { s -> s.copy(buttonLoading = false, buttonEnabled = false) }
    }

    fun changeFieldValueVisibility(field: FieldUi, newValueIsVisible: Boolean) =
        viewModelScope.launch {
            _state.update { s ->
                s.copy(
                    fields = s.fields.updateFieldAndResetErrors(
                        field,
                        field.copy(isValueVisible = newValueIsVisible)
                    )
                )
            }
        }


}