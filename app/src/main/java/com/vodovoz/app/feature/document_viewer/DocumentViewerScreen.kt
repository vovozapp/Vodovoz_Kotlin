package com.vodovoz.app.feature.document_viewer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.rajat.pdfviewer.PdfRendererView
import com.rajat.pdfviewer.compose.PdfRendererViewCompose
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.document_viewer.composables.ZoomableContainer
import com.vodovoz.app.feature.document_viewer.composables.rememberZoomableState
import com.vodovoz.app.feature.document_viewer.model.DocumentViewerState
import com.vodovoz.app.feature.document_viewer.model.DocumentViewerUiState
import kotlinx.coroutines.launch


@Composable
fun DocumentViewerScreen(
    viewModel: DocumentViewerViewModel,
    viewState: DocumentViewerState,
) {
    val document = viewState.currentDocument
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        VodovozTopBar(onBack = { viewModel.navigateBack() }, title = document.description)

        when (document.type) {
            "pdf" -> {
                PdfRendererViewCompose(
                    url = document.src,
                    statusCallBack = object : PdfRendererView.StatusCallBack {
                        override fun onPdfLoadSuccess(absolutePath: String) {
                            viewModel.setUiState(DocumentViewerUiState.Success)
                        }
                    }
                )
            }

            else -> {
                val painter = rememberAsyncImagePainter(document.src)
                val imageState by painter.state.collectAsStateWithLifecycle()

                if (imageState is AsyncImagePainter.State.Success) {
                    val state = rememberZoomableState(contentSize = painter.intrinsicSize)

                    ZoomableContainer(
                        state = state
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    LaunchedEffect(Unit) {
                        viewModel.setUiState(DocumentViewerUiState.Success)
                    }
                }


            }
        }
    }
    if (viewState.uiState is DocumentViewerUiState.Loading) {
        LoadingPlaceholder()
    }
}
