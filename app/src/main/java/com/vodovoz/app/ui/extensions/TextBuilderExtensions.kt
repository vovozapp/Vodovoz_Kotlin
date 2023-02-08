package com.vodovoz.app.ui.extensions

import android.util.Log
import android.widget.TextView
import com.vodovoz.app.util.LogSettings

object TextBuilderExtensions {

    fun TextView.setLimitedText(limitedText: String) {
        this.text = limitedText
        if (this.text.length != limitedText.length) {
            val result = StringBuilder().append(this.text)
            result.delete((result.indices.last - 2), result.indices.last)
            result.append("...")
            this.text = result.toString()
        }
    }

    fun TextView.setExpiredCodeText(
        seconds: Int,
    ) {
        this.text = StringBuilder()
            .append("Повторно код можно будет запросить через ")
            .append(seconds)
            .toString()
    }

    fun TextView.setPriceText(
        price: Int,
        isNegative: Boolean = false,
        itCanBeGift: Boolean = false
    ) {
        if (price == 0 && itCanBeGift) {
            text = "Подарок"
            return
        }
        this.text = StringBuilder()
            .append(when(isNegative && price != 0) {
                true -> "-"
                false -> ""
            })
            .append(price)
            .append(" ₽")
            .toString()
    }

    fun TextView.setMinimalPriceText(price: Int) {
        this.text = StringBuilder()
            .append("от ")
            .append(price)
            .append(" ₽")
            .toString()
    }

    fun TextView.setPricePerUnitText(price: Int) {
        this.text = StringBuilder()
            .append(price)
            .append(" ₽/кг")
            .toString()
    }

    fun TextView.setOrderQuantity(quantity: Int) {
        this.text = StringBuilder()
            .append("x")
            .append(quantity)
            .toString()
    }

    fun TextView.setDepositPriceText(price: Int) {
        this.text = StringBuilder()
            .append("Залоговая стоимость ")
            .append(price)
            .append(" руб")
            .toString()
    }

    fun TextView.setPriceCondition(requireAmount: Int) {
        this.text = StringBuilder()
            .append("При условии покупки от ")
            .append(requireAmount)
            .append(" шт")
            .toString()
    }

    fun TextView.setDiscountPercent(oldPrice: Int, newPrice: Int) {
        this.text = StringBuilder()
            .append((100.0 - ((newPrice.toDouble()/oldPrice.toDouble()) * 100)).toInt())
            .append("%")
            .toString()
    }

    fun TextView.setCommentQuantity(quantity: Int) {
        this.text = StringBuilder()
            .append(quantity)
            .append(" | ")
            .append(" Читать отзывы")
            .toString()
    }

}