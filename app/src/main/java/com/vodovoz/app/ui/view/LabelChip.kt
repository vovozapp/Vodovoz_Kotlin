package com.vodovoz.app.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.vodovoz.app.R

class LabelChip @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defaultStyle: Int = 0,
) : RelativeLayout(context, attributeSet, defaultStyle) {

    private val textView: TextView
    var color: Int = 0
        set(value) {
            field = value
            setTextBackgroundColor(value)
        }

    var text: String = ""
        set(value) {
            field = value
            textView.text = text
        }

    init {
        inflate(context, R.layout.view_label_chip, this)
        textView = findViewById(R.id.tvChip)
    }

    private fun setTextBackgroundColor(color: Int) {
        textView.backgroundTintList = ColorStateList.valueOf(color)
    }
}