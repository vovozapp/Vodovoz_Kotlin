package com.vodovoz.app.util

import com.vodovoz.app.ui.model.PriceUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import java.text.DecimalFormat
import java.text.ParseException
import kotlin.math.roundToInt


val PRICE_FORMATTER = DecimalFormat("#,###")


fun calculateProductPrice(quantity: Int, priceList: List<PriceUI>): Double {
    var totalPrice = 0.0

    for (priceUI in priceList) {
        if (quantity >= priceUI.requiredAmount && quantity <= priceUI.requiredAmountTo) {
            totalPrice = quantity * priceUI.currentPrice
            break
        }
        if (priceList.last() == priceUI) {
            totalPrice = quantity * priceUI.currentPrice
        }
    }

    return totalPrice
}

fun Number.formatPrice(): String {
    return PRICE_FORMATTER.format(this)
}

fun parseNumberOrNull(numberString: String): Long? {
    return try {
        val number = PRICE_FORMATTER.parse(numberString) as Number
        number.toLong()
    } catch (e: ParseException) {
        null
    }
}

fun calculatePrice(productUIList: List<ProductUI>): CalculatedPrices {
    var fullPrice = 0.0
    var discountPrice = 0.0
    var deposit = 0
    var bottlesPrice = 0
    productUIList.forEach { productUI ->
        if (productUI.isBottle) {
            bottlesPrice += if (productUI.depositPrice != 0) {
                productUI.depositPrice * productUI.cartQuantity
            } else {
                productUI.priceList.first().currentPrice.roundToInt() * productUI.cartQuantity
            }
        } else {
            if (productUI.depositPrice != 0) {
                deposit += productUI.depositPrice * productUI.cartQuantity
            }
            val price = when (productUI.priceList.size) {
                1 -> productUI.priceList.first()
                else -> {
                    val sortedPriceList =
                        productUI.priceList.sortedByDescending { it.requiredAmount }

                    val minimalPrice =
                        if (sortedPriceList.first().requiredAmountTo == 0 && productUI.cartQuantity >= sortedPriceList.first().requiredAmount) {
                            sortedPriceList.find { it.requiredAmountTo == 0 && productUI.cartQuantity >= it.requiredAmount }
                        } else {
                            sortedPriceList.find { productUI.cartQuantity in it.requiredAmount..it.requiredAmountTo }
                        }
                    minimalPrice
                }
            }
            price?.let {
                fullPrice += if (price.oldPrice.roundToInt() == 0) {
                    price.currentPrice * productUI.cartQuantity
                } else {
                    price.oldPrice * productUI.cartQuantity
                }
                if (productUI.totalDisc > 0) {
                    discountPrice += (productUI.totalDisc * productUI.cartQuantity)
                } else if (price.oldPrice > 0) {
                    discountPrice += (price.oldPrice - price.currentPrice) * productUI.cartQuantity
                }
            }
        }
    }

    debugLog {
        "fullPrice $fullPrice\n" +
                "discountPrice $discountPrice\n" +
                "deposit $deposit\n" +
                "bottlesPrice $bottlesPrice\n"
    }

    deposit -= bottlesPrice
    if (deposit < 0) deposit = 0
    val total = fullPrice + deposit - discountPrice
    if (discountPrice - discountPrice.toInt() > 0) discountPrice++
    return CalculatedPrices(fullPrice.toInt(), discountPrice.toInt(), deposit, total.toInt())
}

data class CalculatedPrices(
    val fullPrice: Int,
    val discountPrice: Int,
    val deposit: Int,
    val total: Int,
)