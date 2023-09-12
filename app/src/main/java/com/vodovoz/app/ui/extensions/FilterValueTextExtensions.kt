package com.vodovoz.app.ui.extensions

import android.widget.TextView
import com.vodovoz.app.ui.model.FilterValueUI

object FilterValueTextExtensions {

    fun TextView.setFilterValue(filterValueList: MutableList<FilterValueUI>) {
        val value = StringBuilder()
        filterValueList.forEachIndexed { index, filterValueUI ->
            value.append(filterValueUI.value)
            if (index != filterValueList.size - 1) {
                value.append(", ")
            }
        }
        text = value.toString()
    }
}