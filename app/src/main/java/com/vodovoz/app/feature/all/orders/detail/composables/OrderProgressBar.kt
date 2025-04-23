package com.vodovoz.app.feature.all.orders.detail.composables

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.design_system.VodovozTheme

@Suppress("NonSkippableComposable")
@Composable
fun OrderProgressBar(
    statuses: List<String>,
    currentStep: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceTint,
    inactiveBarColor: Color = MaterialTheme.colorScheme.surface,
    circleRadius: Dp = 8.dp,
    lineThickness: Dp = 4.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    textMargin: Dp = 9.dp,
    textActiveColor: Color = activeColor,
    textInactiveColor: Color = inactiveColor,
) {
    val density = LocalDensity.current
    val textSize = textStyle.fontSize
    val textSizePx = with(density){ textSize.toPx() }
    val totalHeight =
        with(density) { circleRadius * 2 + lineThickness + textMargin + textSizePx.toDp() }
    val textTypeface = rememberTypefaceFromStyle(style = textStyle)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(totalHeight)
    ) {
        val w = size.width
        val n = statuses.size

        val cy = circleRadius.toPx()
        val cr = circleRadius.toPx()
        val lt = lineThickness.toPx()
        val marginPx = textMargin.toPx()

        val startX = cr
        val endX = w - cr
        val step = (endX - startX) / (n - 1).coerceAtLeast(1)

        val paint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            typeface = textTypeface
            this.textSize = textSizePx
        }

        statuses.forEachIndexed { i, label ->
            val cx = startX + i * step

            if (i < n - 1) {
                drawLine(
                    color = if (i < currentStep) activeColor else inactiveBarColor,
                    start = Offset(cx + cr, cy),
                    end = Offset(cx + step - cr, cy),
                    strokeWidth = lt
                )
            }

            drawCircle(
                color = if (i <= currentStep) activeColor else inactiveBarColor,
                radius = cr,
                center = Offset(cx, cy)
            )

            paint.color =
                if (i <= currentStep) textActiveColor.toArgb() else textInactiveColor.toArgb()

            drawContext.canvas.nativeCanvas.drawText(
                label,
                cx,
                cy + cr + marginPx + textSizePx,
                paint
            )
        }
    }
}

@Composable
fun rememberTypefaceFromStyle(style: TextStyle): Typeface {
    val resolver = LocalFontFamilyResolver.current
    return remember(resolver, style) {
        resolver.resolve(
            fontFamily = style.fontFamily,
            fontWeight = style.fontWeight ?: FontWeight.Normal,
            fontStyle = style.fontStyle ?: FontStyle.Normal,
            fontSynthesis = style.fontSynthesis ?: FontSynthesis.All,
        ).value as Typeface
    }
}

@Composable
@Preview
fun ExampleOfProgressBar(modifier: Modifier = Modifier) {
    VodovozTheme {
        val orderStatuses = listOf("Принят", "В обработке", "В пути", "Выполнен")
        OrderProgressBar(
            statuses = orderStatuses,
            currentStep = 1,
            modifier = modifier.fillMaxWidth()
        )
    }
}
