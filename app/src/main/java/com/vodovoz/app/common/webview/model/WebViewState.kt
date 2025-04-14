package com.vodovoz.app.common.webview.model

data class WebViewState(
    val title: String = "",
    val url: String = "",
    val uiState: WebViewUiState = WebViewUiState.Loading
)
