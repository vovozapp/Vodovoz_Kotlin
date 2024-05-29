package com.vodovoz.app.ui.extensions

import android.widget.TextView
import java.util.Locale
import kotlin.math.roundToInt

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
        price: Int?,
        isNegative: Boolean = false,
        itCanBeGift: Boolean = false
    ) {
        if (price == null) return
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

    fun TextView.setDoublePriceText(
        price: Double?,
    ) {
        if (price == null) return
        val doublePrice = String.format(locale = Locale.US, "%.2f", price)
        val intPrice = price.toInt()
        this.text = StringBuilder()
            .append( if(price - intPrice > 0)
                doublePrice else intPrice)
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
            .append("X ")
            .append(quantity)
            .append(" шт")
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

    fun TextView.setDiscountPercent(oldPrice: Double, newPrice: Double) {
        this.text = StringBuilder()
            .append((100.0 - ((newPrice/oldPrice) * 100)).roundToInt())
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