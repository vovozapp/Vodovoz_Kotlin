package com.vodovoz.app.ui.extensions

import android.util.Log
import android.widget.TextView
import com.vodovoz.app.util.LogSettings

object PriceTextBuilderExtensions {

    fun TextView.setPriceText(price: Int) {
        this.text = StringBuilder()
            .append(price)
            .append(" Р")
            .toString()
    }

    fun TextView.setDiscountText(oldPrice: Int, newPrice: Int) {
        this.text = StringBuilder()
            .append((100.0 - ((newPrice.toDouble()/oldPrice.toDouble()) * 100)).toInt())
            .append("%")
            .toString()
    }

}