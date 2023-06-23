package com.vodovoz.app.util

import com.vodovoz.app.ui.model.ProductUI
import timber.log.Timber

fun calculatePrice(productUIList: List<ProductUI>): CalculatedPrices {
    var fullPrice = 0
    var discountPrice = 0.0
    var deposit = 0
    var bottlesPrice = 0
    productUIList.forEach { productUI ->
        if (productUI.isBottle) {
            bottlesPrice += productUI.depositPrice * productUI.cartQuantity
        } else {
            if (productUI.depositPrice != 0) {
                deposit += productUI.depositPrice * productUI.cartQuantity
            }
            val price = when (productUI.priceList.size) {
                1 -> productUI.priceList.first()
                else -> {
                    val sortedPriceList = productUI.priceList.sortedByDescending { it.requiredAmount }

                    val minimalPrice =  if (sortedPriceList.first().requiredAmountTo == 0 && productUI.cartQuantity >= sortedPriceList.first().requiredAmount) {
                        sortedPriceList.find { it.requiredAmountTo == 0 && productUI.cartQuantity >= it.requiredAmount }
                    } else {
                        sortedPriceList.find { productUI.cartQuantity in it.requiredAmount .. it.requiredAmountTo }
                    }
                    minimalPrice
                }
            }
            price?.let {
                fullPrice += price.oldPrice * productUI.cartQuantity
                discountPrice += productUI.totalDisc * productUI.cartQuantity
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
    val total = fullPrice + deposit - discountPrice.toInt()
    return CalculatedPrices(fullPrice, discountPrice.toInt(), deposit, total)
}

data class CalculatedPrices(
    val fullPrice: Int,
    val discountPrice: Int,
    val deposit: Int,
    val total: Int
)