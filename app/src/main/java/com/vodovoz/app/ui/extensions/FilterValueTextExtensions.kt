package com.vodovoz.app.ui.extensions

import android.widget.TextView
import com.vodovoz.app.ui.model.FilterValueUI

object FilterValueTextExtensions {

    fun TextView.setFilterValue(filterValueList: MutableList<FilterValueUI>) {
        val value = StringBuilder()
        for (index in filterValueList.indices) {
            value.append(filterValueList[index].value)
            if (index != filterValueList.size - 1) {
                value.append(", ")
            }
        }
        text = value.toString()
    }
}