package com.vodovoz.app.common.webview

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vodovoz.app.common.webview.model.WebViewState
import com.vodovoz.app.common.webview.model.WebViewUiState
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import kotlinx.coroutines.launch

@Composable
fun WebViewScreen(viewModel: WebViewViewModel, viewState: WebViewState) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (viewState.title.isNotEmpty()) {
            VodovozTopBar(
                onBack = {
                    viewModel.navigateBack()
                },
                title = viewState.title
            )
        }
        Box {
            when (viewState.uiState) {
                WebViewUiState.Error -> {
                    NetworkErrorPlaceholder { viewModel.setUiState(WebViewUiState.Loading) }
                }

                else -> {
                    WebView(
                        url = viewState.url,
                        onLoadingFinished = { viewModel.setUiState(WebViewUiState.NotLoading) },
                        onError = { viewModel.setUiState(WebViewUiState.Error) }
                    )
                }
            }
        }
    }

    if (viewState.uiState is WebViewUiState.Loading) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize()
                .size(40.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = Color.Transparent,
            strokeWidth = 4.dp
        )
    }
}


@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebView(
    modifier: Modifier = Modifier,
    url: String,
    onLoadingFinished: () -> Unit,
    onError: () -> Unit,
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }

    val webClient = remember { WebClient(onError = onError) }
    val chromeClient = remember { ProgressWebChromeClient(onPageVisible = onLoadingFinished) }

    DisposableEffect(Unit) {
        webView.webViewClient = webClient
        webView.webChromeClient = chromeClient
        onDispose { webView.destroy() }

    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )

    LaunchedEffect(url) {
        if (url.contains("#")) {
            webView.loadDataWithBaseURL(
                url.substringBefore("#"),
                "",
                "text/html",
                "utf-8",
                null
            )
        } else {
            webView.loadUrl(url)
        }
    }


}

private class WebClient(
    private val onError: () -> Unit,
) : WebViewClient() {

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?,
    ) {
        super.onReceivedError(view, request, error)
        if (error != null) {
            onError()
        }
    }

}

private class ProgressWebChromeClient(
    private val onPageVisible: () -> Unit,
) : WebChromeClient() {

    private var isCallbackCalled = false

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        if (newProgress >= 20 && !isCallbackCalled) {
            isCallbackCalled = true
            onPageVisible()
        }
    }

}
