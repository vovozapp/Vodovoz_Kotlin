package com.vodovoz.app.ui.extensions

import android.widget.TextView

object PriceTextBuilderExtensions {

    fun TextView.setPriceText(price: Int) {
        this.text = StringBuilder()
            .append(price)
            .append(" Р")
            .toString()
    }

    fun TextView.setDiscountText(oldPrice: Int, newPrice: Int) {
        this.text = StringBuilder()
            .append((100.0 - (newPrice/oldPrice) * 100).toInt())
            .append("%")
            .toString()
    }

}