package com.vodovoz.app.feature.order_question_fragment.model

import com.vodovoz.app.design_system.model.ErrorDataUi

sealed interface OrderQuestionUiState {

    data object Loading : OrderQuestionUiState
    data object Fields : OrderQuestionUiState
    data class Success(
        val placeholderData: ErrorDataUi,
    ) : OrderQuestionUiState

    data object Error : OrderQuestionUiState

}