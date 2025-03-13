package com.vodovoz.app.util

import androidx.compose.ui.graphics.Color

fun Color.Companion.fromHexOrUnspecified(hexString: String) = try {
    Color(android.graphics.Color.parseColor(hexString))
} catch (_: Exception) {
    Unspecified
}

fun Color.Companion.fromHexOrNull(hexString: String) = try {
    Color(android.graphics.Color.parseColor(hexString))
} catch (_: Exception) {
    null
}
