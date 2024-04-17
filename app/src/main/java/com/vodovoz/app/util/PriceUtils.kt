package com.vodovoz.app.util

import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import kotlin.math.roundToInt

fun calculatePrice(productUIList: List<ProductUI>): CalculatedPrices {
    var fullPrice = 0
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
                    price.currentPrice.roundToInt() * productUI.cartQuantity
                } else {
                    price.oldPrice.roundToInt() * productUI.cartQuantity
                }
                if(productUI.totalDisc > 0) {
                    discountPrice += (productUI.totalDisc * productUI.cartQuantity)
                } else if(price.oldPrice  > 0) {
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
    val total = fullPrice + deposit - discountPrice.toInt()
    return CalculatedPrices(fullPrice, discountPrice.toInt(), deposit, total)
}

data class CalculatedPrices(
    val fullPrice: Int,
    val discountPrice: Int,
    val deposit: Int,
    val total: Int,
)