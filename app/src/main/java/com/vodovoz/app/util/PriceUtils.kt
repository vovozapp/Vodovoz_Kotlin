package com.vodovoz.app.util

import com.vodovoz.app.ui.model.ProductUI
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.roundToInt

fun calculatePrice(productUIList: List<ProductUI>): CalculatedPrices {
    var fullPrice = 0
    var discountPrice = 0
    var deposit = 0
    var bottlesPrice = 0
    productUIList.forEach { productUI ->
        if (productUI.isBottle) {
            if(productUI.depositPrice != 0){
                bottlesPrice += productUI.depositPrice * productUI.cartQuantity
            } else {
                bottlesPrice += productUI.priceList.first().currentPrice * productUI.cartQuantity
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
                if (price.oldPrice == 0) {
                    fullPrice += price.currentPrice * productUI.cartQuantity
                } else {
                    fullPrice += price.oldPrice * productUI.cartQuantity
                }

                val totalDisc = if(abs(productUI.totalDisc.roundToInt().toDouble() - productUI.totalDisc) == 0.5){
                    productUI.totalDisc.toInt()
                } else {
                    productUI.totalDisc.roundToInt()
                }

                discountPrice += totalDisc * productUI.cartQuantity
            }
        }
    }

    Timber.d("" +
            "fullPrice $fullPrice\n" +
            "discountPrice $discountPrice\n" +
            "deposit $deposit\n" +
            "bottlesPrice $bottlesPrice\n")

    deposit -= bottlesPrice
    if (deposit < 0) deposit = 0
    val total = fullPrice + deposit - discountPrice
    return CalculatedPrices(fullPrice, discountPrice, deposit, total)
}

data class CalculatedPrices(
    val fullPrice: Int,
    val discountPrice: Int,
    val deposit: Int,
    val total: Int
)