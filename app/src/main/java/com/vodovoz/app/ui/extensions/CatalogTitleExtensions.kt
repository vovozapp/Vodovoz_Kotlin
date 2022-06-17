package com.vodovoz.app.ui.extensions

import android.widget.TextView

object CatalogTitleExtensions {

    private const val INDENT = "      "

    fun TextView.setNameWithIndent(
        title: String,
        nestingPosition: Int
    ) = StringBuilder().also {
        for (index in 0 until nestingPosition) it.append(INDENT)
        it.append(title)
        text = it.toString()
    }

}