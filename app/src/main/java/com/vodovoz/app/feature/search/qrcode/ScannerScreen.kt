package com.vodovoz.app.feature.search.qrcode

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.vodovoz.app.feature.search.qrcode.model.BarcodeAnalyzer
import timber.log.Timber

@Composable
fun ScannerScreen() {
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(localContext)
    }

    Box {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = Preview.Builder().build()
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                preview.surfaceProvider = previewView.surfaceProvider

                val imageAnalysis = ImageAnalysis.Builder().build()

                imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    BarcodeAnalyzer(context)
                )

                runCatching {
                    cameraProviderFuture.get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                }.onFailure {
                    Timber.tag("CAMERA").e(it, "Camera bind error " + it.localizedMessage)
                }
                previewView
            }
        )

        DarkScreenWithHole()

    }
}

@Composable
fun DarkScreenWithHole(modifier: Modifier = Modifier) {
    val width = 240.dp
    val height = 150.dp

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val left = centerX - width.toPx() / 2
            val top = centerY - height.toPx() / 2

            drawRect(
                color = Color.Black.copy(alpha = 0.7f)
            )

            drawRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(240.dp.toPx(), 150.dp.toPx()),
                blendMode = BlendMode.Clear
            )
        }
    }
}
