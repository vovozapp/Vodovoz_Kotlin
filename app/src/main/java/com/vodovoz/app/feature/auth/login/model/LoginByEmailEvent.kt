package com.vodovoz.app.feature.auth.login.model

sealed interface LoginByEmailEvent {

    data object GoBack: LoginByEmailEvent
}