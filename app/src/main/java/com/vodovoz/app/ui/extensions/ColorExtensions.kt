package com.vodovoz.app.ui.extensions

import android.graphics.Color
import kotlin.math.roundToInt


object ColorExtensions {


    fun Int.getColorWithAlpha(ratio: Float): Int {
        val alpha = (Color.alpha(this) * ratio).roundToInt()
        val r = Color.red(this)
        val g = Color.green(this)
        val b = Color.blue(this)
        return Color.argb(alpha, r, g, b)
    }

}