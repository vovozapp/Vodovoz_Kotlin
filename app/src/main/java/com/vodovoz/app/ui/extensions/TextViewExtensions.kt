package com.vodovoz.app.ui.extensions

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import com.vodovoz.app.R

object TextViewExtensions {

    fun TextView.setDrawableColor(@ColorRes color: Int) {
        compoundDrawables.filterNotNull().forEach {
            it.colorFilter = PorterDuffColorFilter(getColor(context, R.color.green), PorterDuff.Mode.SRC_IN)
        }
    }

}