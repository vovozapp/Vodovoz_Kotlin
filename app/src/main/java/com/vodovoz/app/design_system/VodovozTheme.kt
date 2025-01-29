package com.vodovoz.app.design_system

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider


private val lightColors = lightColorScheme(
    primary = blue,
    primaryContainer = lightBlue,
    secondary = green,
    onBackground = black,
    background = white,
    surfaceVariant = grey,
    surface = lightGrey,
    surfaceTint = deepGrey,
    error = red,
    tertiary = yellow,
    outline = greyBlue
)

private val extendedLightColors = ExtendedColors(
    primaryVariant = blueDisable
)


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
            LocalExtendedTypography provides extendedTypography
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