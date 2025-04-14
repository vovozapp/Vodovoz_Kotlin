package com.vodovoz.app.common.webview.model

sealed interface WebViewEvents {

    data object GoBack: WebViewEvents
}