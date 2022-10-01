package com.vodovoz.app.ui.extensions

import android.graphics.Color
import kotlin.math.roundToInt


object ColorExtensions {


    fun Int.getColorWithAlpha(ratio: Float): Int {
        var newColor = 0
        val alpha = (Color.alpha(this) * ratio).roundToInt()
        val r = Color.red(this)
        val g = Color.green(this)
        val b = Color.blue(this)
        newColor = Color.argb(alpha, r, g, b)
        return newColor
    }

}