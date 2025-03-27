package com.vodovoz.app.feature.profile.change_password.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.feature.preorder.model.FieldUi

@Immutable
data class ChangePasswordState(
    val fields: List<FieldUi> = emptyList(),
    val title: String = "",
    val uiState: ChangePasswordUiState = ChangePasswordUiState.Loading,
    val buttonEnabled: Boolean = false,
    val buttonLoading: Boolean = false
)
