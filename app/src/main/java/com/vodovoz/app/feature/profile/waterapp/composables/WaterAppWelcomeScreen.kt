package com.vodovoz.app.feature.profile.waterapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton

@Composable
fun WaterAppWelcomeScreen(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    onStartClick: () -> Unit,
) {


    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .height(56.dp)
                .padding(horizontal = 16.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close_circle),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable { onCloseClick() },
                tint = MaterialTheme.colorScheme.background.copy(0.8f)
            )
        }

        val primaryColor = MaterialTheme.colorScheme.primary

        val largeRadialGradient = remember {
            object : ShaderBrush() {
                override fun createShader(size: Size): Shader {
                    val biggerDimension = maxOf(size.height, size.width)
                    return RadialGradientShader(
                        colors = listOf(primaryColor.copy(0.65f), primaryColor),
                        center = size.center,
                        radius = biggerDimension / 4f,
                        colorStops = listOf(0f, 0.90f)
                    )
                }
            }
        }

        val waveImage = ImageBitmap.imageResource(id = R.drawable.water_app_wave)


        Column(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val imgWidth = waveImage.width.toFloat()
                    val imgHeight = waveImage.height.toFloat()
                    val containerWidth = size.width
                    val containerHeight = size.height

                    val scale = maxOf(containerWidth / imgWidth, containerHeight / imgHeight)

                    val scaledWidth = imgWidth * scale
                    val scaledHeight = imgHeight * scale

                    val topLeft = Offset(
                        (containerWidth - scaledWidth) / 2,
                        (containerHeight - scaledHeight) / 2
                    )

                    onDrawWithContent {
                        drawRect(largeRadialGradient)


                        drawImage(
                            image = waveImage,
                            srcOffset = IntOffset(0, 0),
                            srcSize = IntSize(waveImage.width, waveImage.height),
                            dstOffset = IntOffset(topLeft.x.toInt(), topLeft.y.toInt()),
                            dstSize = IntSize(scaledWidth.toInt(), scaledHeight.toInt()),
                            blendMode = BlendMode.Screen
                        )

                        drawContent()
                    }
                }
        ) {
            Text(
                modifier = Modifier.padding(start = 32.dp, end = 10.dp),
                text = stringResource(R.string.water_app_welcome_title),
                color = MaterialTheme.colorScheme.background,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(
                        Font(R.font.roboto_bold)
                    ),
                    lineHeight = 35.sp,
                    lineHeightStyle = LineHeightStyle(
                        LineHeightStyle.Alignment.Center,
                        LineHeightStyle.Trim.None
                    ),
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    ),
                )
            )

            Text(
                modifier = Modifier.padding(top = 20.dp, start = 32.dp, end = 10.dp),
                text = stringResource(R.string.water_app_welcome_text),
                color = MaterialTheme.colorScheme.background,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily(
                        Font(R.font.roboto_regular)
                    ),
                    lineHeight = 26.sp,
                    lineHeightStyle = LineHeightStyle(
                        LineHeightStyle.Alignment.Center,
                        LineHeightStyle.Trim.None
                    ),
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    ),
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            VodovozButton(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 30.dp),
                text = stringResource(R.string.start),
                onClick = { onStartClick() },
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = Color(0xFF05A4FF),
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}