package com.vodovoz.app.design_system

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


val blue = Color(0xFF05A4FF)
val blueDisable = Color(0xFF96D9FF)
val greyBlue = Color(0xFFDCECF6)
val lightBlue = Color(0xFFEEF8FE)

val white = Color(0xFFFFFFFF)
val lightGrey = Color(0xFFF3F3F3)
val grey = Color(0xFFEAEAEA)
val deepGrey = Color(0xFF8689A2)
val black = Color(0xFF222222)

val green = Color(0xFF32CB5D)
val red = Color(0xFFF91155)
val yellow = Color(0xFFFFCE33)
val violet = Color(0xFFA56FFD)


data class ExtendedColors(
    val primaryVariant: Color,
)

val LocalExtendedColors = staticCompositionLocalOf<ExtendedColors> {
    error("extended colors didn't implement")
}

