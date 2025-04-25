package com.vodovoz.app.feature.cancel_order_fragment.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.ColorfulButtonUi

@Immutable
data class CancelOrderState(
    val uiState: CancelOrderUiState = CancelOrderUiState.Loading,
    val title: String = "",
    val warningText: String = "",
    val description: String = "",
    val checkboxesNames: List<String> = emptyList(),
    val currentCheckboxName: String = "",
    val button: ColorfulButtonUi = ColorfulButtonUi.Empty,
)
