package com.vodovoz.app.ui.extensions

import java.util.*
import java.util.Date

object Date {

    fun Date.get(value: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar[value]
    }

    fun Date.dd(): String {
        val day = get(Calendar.DAY_OF_MONTH)
        return if (day > 9) day.toString()
        else "0$day"
    }

    fun Date.mm(): String {
        val month = get(Calendar.MONTH) + 1
        return if (month > 9) month.toString()
        else "0$month"
    }

    fun from(day: Int, month: Int, year: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        return calendar.time
    }

}