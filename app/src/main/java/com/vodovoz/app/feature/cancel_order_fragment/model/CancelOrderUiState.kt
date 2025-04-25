package com.vodovoz.app.feature.cancel_order_fragment.model

sealed interface CancelOrderUiState {

    data object Loading: CancelOrderUiState
    data object Body: CancelOrderUiState
    data object Error: CancelOrderUiState


}