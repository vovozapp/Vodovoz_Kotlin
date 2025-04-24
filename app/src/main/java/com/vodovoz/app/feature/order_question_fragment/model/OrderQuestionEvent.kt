package com.vodovoz.app.feature.order_question_fragment.model

sealed interface OrderQuestionEvent {

    data object GoBack: OrderQuestionEvent

    data class ShowToast(val message: String): OrderQuestionEvent

}