package com.vodovoz.app.util

import androidx.compose.ui.graphics.Color

fun Color.Companion.fromHexOrTransparent(hexString: String) = try {
    Color(android.graphics.Color.parseColor(hexString))
} catch (_: Exception) {
    Transparent
}
