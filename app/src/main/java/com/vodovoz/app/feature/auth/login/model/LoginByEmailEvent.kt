package com.vodovoz.app.feature.auth.login.model

sealed interface LoginByEmailEvent {
    data class GoToWebView(val url: String, val title: String) : LoginByEmailEvent
    data object GoBack: LoginByEmailEvent
    data object GoToRegister : LoginByEmailEvent
    data object RefreshAll : LoginByEmailEvent
    data object GoToRecoverPassword : LoginByEmailEvent
}