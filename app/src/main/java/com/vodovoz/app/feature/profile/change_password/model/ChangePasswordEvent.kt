package com.vodovoz.app.feature.profile.change_password.model

sealed interface ChangePasswordEvent {
    data object GoBack : ChangePasswordEvent
    data object Logout : ChangePasswordEvent
    data class ShowSnackbar(val message: String) : ChangePasswordEvent
}