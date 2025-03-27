package com.vodovoz.app.feature.profile.change_password.model

sealed interface ChangePasswordUiState {

    data object Loading: ChangePasswordUiState
    data object ChangePassword: ChangePasswordUiState
    data object PasswordChanged: ChangePasswordUiState

}