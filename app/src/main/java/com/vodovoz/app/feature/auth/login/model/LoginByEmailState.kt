package com.vodovoz.app.feature.auth.login.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.feature.auth.login.composables.LoginByEmailUiState
import com.vodovoz.app.feature.preorder.model.FieldUi

@Immutable
data class LoginByEmailState(
    val title: String = "",
    val description: String = "",
    val fields: List<FieldUi> = emptyList(),
    val buttons: List<ColorfulButtonUi> = emptyList(),
    val uiState: LoginByEmailUiState = LoginByEmailUiState.Loading
)
