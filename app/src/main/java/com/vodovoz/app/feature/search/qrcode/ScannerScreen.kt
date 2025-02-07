package com.vodovoz.app.feature.search.qrcode

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
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

        Test()
    }
}

@Composable
fun ScannerTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .consumeWindowInsets(WindowInsets.statusBars)
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_close_stories),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    onClick = {},
                    interactionSource = null,
                    indication = ripple(radius = 20.dp)
                ),
            tint = MaterialTheme.colorScheme.background
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.ic_lighting),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    onClick = {},
                    interactionSource = null,
                    indication = ripple(radius = 20.dp)
                ),
            tint = MaterialTheme.colorScheme.background
        )
    }

}

@Composable
fun Test(modifier: Modifier = Modifier) {

    val bordersPainter = painterResource(id = R.drawable.ic_scanner_borders)



    Column(
        modifier = modifier
            .fillMaxSize()
            .drawWithContent {
                drawRect(color = Color.Black.copy(alpha = 0.7f))
                drawContent()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScannerTopBar()


        Spacer(modifier = Modifier.weight(1.05f))

        Text(
            text = stringResource(R.string.scanner_screen),
            modifier = Modifier.padding(horizontal = 34.dp),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Box(
            Modifier
                .padding(top = 52.dp)
                .width(240.dp)
                .height(160.dp)
                .clip(RoundedCornerShape(18.dp))
                .align(Alignment.CenterHorizontally)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawWithBlendMode(BlendMode.Clear)
            )
            Image(
                painter = bordersPainter,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_barcode),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 60.dp)
                .width(50.dp)
                .height(35.dp),
            tint = Color.White
        )

        Spacer(modifier = Modifier.weight(2f))
    }
}

fun Modifier.drawWithBlendMode(blendMode: BlendMode): Modifier = drawWithContent {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply { this.blendMode = blendMode }
        canvas.saveLayer(size.toRect(), paint)
        drawContent()
        canvas.restore()
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun TestPreview() {
    VodovozTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Test()
        }
    }
}
