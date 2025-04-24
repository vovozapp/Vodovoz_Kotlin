package com.vodovoz.app.feature.order_question_fragment.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.feature.preorder.model.FieldUi

@Immutable
data class OrderQuestionState(
    val title: String = "",
    val description: String = "",
    val fields: List<FieldUi> = emptyList(),
    val uiState: OrderQuestionUiState = OrderQuestionUiState.Loading,
    val button: ColorfulButtonUi = ColorfulButtonUi.Empty,
)
