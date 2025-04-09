package com.vodovoz.app.feature.auth.login.composables

sealed interface LoginByEmailUiState {

    data object Error: LoginByEmailUiState
    data object Loading: LoginByEmailUiState
    data object Success: LoginByEmailUiState


}