package com.vodovoz.app.feature.order_question_fragment.model

import com.vodovoz.app.design_system.model.VodovozPlaceholderUi

sealed interface OrderQuestionUiState {

    data object Loading : OrderQuestionUiState
    data object Fields : OrderQuestionUiState
    data class Success(
        val placeholderData: VodovozPlaceholderUi,
    ) : OrderQuestionUiState

    data object Error : OrderQuestionUiState

}