package com.vodovoz.app.util

import com.vodovoz.app.ui.model.ProductUI

fun calculatePrice(productUIList: List<ProductUI>): Triple<Int, Int, Int> {
    var fullPrice = 0
    var discountPrice = 0
    var deposit = 0
    var bottlesPrice = 0
    productUIList.forEach { productUI ->
        if (productUI.isBottle) {
            bottlesPrice += productUI.priceList.first().currentPrice * productUI.cartQuantity
        } else {
            if (productUI.depositPrice != 0) {
                deposit += productUI.depositPrice * productUI.cartQuantity
            }
            val price = when (productUI.priceList.size) {
                1 -> productUI.priceList.first()
                else -> {
                    val sortedPriceList = productUI.priceList
                        .sortedBy { it.requiredAmount }
                        .reversed()
                    val defaultPrice = sortedPriceList.last()
                    val rightPrice = sortedPriceList.find { productUI.cartQuantity >= it.requiredAmount }
                    rightPrice?.let {
                        discountPrice += (defaultPrice.currentPrice - rightPrice.currentPrice) * productUI.cartQuantity
                    }
                    rightPrice
                }
            }
            price?.let {
                when(price.oldPrice) {
                    0 -> fullPrice += price.currentPrice * productUI.cartQuantity
                    else -> {
                        fullPrice += price.oldPrice * productUI.cartQuantity
                        discountPrice += (price.oldPrice - price.currentPrice) * productUI.cartQuantity
                    }
                }
            }
        }
    }

    deposit -= bottlesPrice
    if (deposit < 0) deposit = 0
    return Triple(fullPrice, discountPrice, deposit)
}