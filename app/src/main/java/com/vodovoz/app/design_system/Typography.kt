package com.vodovoz.app.design_system

import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R


val vodovozTypography = Typography(
    labelMedium = TextStyle(
        fontSize = 13.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Normal
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Normal
    ),
    titleLarge = TextStyle(
        fontSize = 24.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.18.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Medium
    ),
    titleMedium = TextStyle(
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Medium
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Medium
    ),
    headlineSmall = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Medium
    ),
    bodySmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp,
        fontFamily = robotoFontFamily,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Normal
    ),

    )

val extendedTypography = ExtendedTypography(
    buttonMedium = TextStyle(
        fontSize = 17.sp,
        lineHeight = 24.sp,
        fontFamily = robotoFontFamily,
        letterSpacing = 0.15.sp,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Medium
    ),
    buttonSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontFamily = robotoFontFamily,
        letterSpacing = 0.1.sp,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Medium
    ),
    labelExtraSmall = TextStyle(
        fontSize = 9.sp,
        lineHeight = 24.sp,
        fontFamily = robotoFontFamily,
        letterSpacing = 0.1.sp,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Normal
    ),
    labelExtraSmallVariant = TextStyle(
        fontSize = 10.sp,
        lineHeight = 16.sp,
        fontFamily = robotoFontFamily,
        letterSpacing = 0.4.sp,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Normal
    ),
    labelMediumVariant = TextStyle(
        fontSize = 12.sp,
        lineHeight = 24.sp,
        fontFamily = robotoFontFamily,
        letterSpacing = 0.1.sp,
        lineHeightStyle = LineHeightStyle(
            LineHeightStyle.Alignment.Center,
            LineHeightStyle.Trim.None
        ),
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        fontWeight = FontWeight.Medium
    )
)

class ExtendedTypography(
    val buttonMedium: TextStyle = TextStyle.Default,
    val buttonSmall: TextStyle = TextStyle.Default,
    val labelExtraSmall: TextStyle = TextStyle.Default,
    val labelExtraSmallVariant: TextStyle = TextStyle.Default,
    val labelMediumVariant: TextStyle = TextStyle.Default,
)

val LocalExtendedTypography =
    staticCompositionLocalOf<ExtendedTypography> { error("extended typography didn't implement") }


private val robotoFontFamily
    get() = FontFamily(
        Font(R.font.roboto_regular, FontWeight.Normal),
        Font(R.font.roboto_medium, FontWeight.Medium),
        Font(R.font.roboto_semibold, FontWeight.SemiBold),
        Font(R.font.roboto_bold, FontWeight.Bold),
    )