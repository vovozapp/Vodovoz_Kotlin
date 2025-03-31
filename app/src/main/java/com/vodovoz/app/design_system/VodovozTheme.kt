package com.vodovoz.app.design_system

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme


private val lightColors = lightColorScheme(
    primary = blue,
    primaryContainer = lightBlue,
    secondary = green,
    onBackground = black,
    background = white,
    surfaceVariant = grey,
    surface = lightGrey,
    onSurface = lightDeepGrey,
    surfaceTint = deepGrey,
    error = red,
    tertiary = yellow,
    outline = greyBlue,
    outlineVariant = greyVariant
)

private val extendedLightColors = ExtendedColors(
    primaryVariant = blueDisable
)

val vodovozTextLinkStyle: TextLinkStyles
    @Composable
    get(){
        return TextLinkStyles(
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ).toSpanStyle()
        )
    }

@Composable
fun VodovozTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isDarkTheme) lightColors else lightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = vodovozTypography,
        shapes = vodovozShapes
    ) {
        CompositionLocalProvider(
            LocalExtendedColors provides extendedLightColors,
            LocalExtendedTypography provides extendedTypography,
            LocalShimmerTheme provides VodovozShimmerTheme
        ) {
            content()
        }
    }
}

data object ExtendedTheme {

    val colorScheme: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current

    val typography: ExtendedTypography
        @Composable
        get() = LocalExtendedTypography.current

}

private val VodovozShimmerTheme
    @Composable
    get() = defaultShimmerTheme.copy(
        blendMode = BlendMode.DstAtop,
        shimmerWidth = 180.dp,
        shaderColors = listOf(
            Color.Transparent,
            MaterialTheme.colorScheme.background,
            Color.Transparent,
        ),
        shaderColorStops = listOf(
            0.0f,
            0.5f,
            1.0f,
        ),
        animationSpec = infiniteRepeatable(
            animation = tween(
                1000,
                easing = LinearEasing,
                delayMillis = 300,
            ),
            repeatMode = RepeatMode.Restart,
        )
    )
