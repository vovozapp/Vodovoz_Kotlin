package com.vodovoz.app.common.webview.model

sealed interface WebViewUiState {

    data object Loading: WebViewUiState
    data object NotLoading: WebViewUiState
    data object Error: WebViewUiState


}