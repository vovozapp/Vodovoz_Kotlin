package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.mapper.PriceMapper.mapToUI
import com.vodovoz.app.ui.extensions.TextBuilderExtensions.setPriceText
import com.vodovoz.app.ui.model.PriceUI
import com.vodovoz.app.ui.model.ProductUI

object ProductMapper {

    fun List<ProductEntity>.mapToUI(): List<ProductUI> = mutableListOf<ProductUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun ProductEntity.mapToUI() = ProductUI(
        id = id,
        name = name,
        isBottle = isBottle,
        detailPicture = detailPicture,
        isFavorite = isFavorite,
        leftItems = leftItems,
        pricePerUnit = pricePerUnit,
        priceList = priceList.mapToUI(),
        status = status,
        statusColor = statusColor,
        rating = rating.toFloat(),
        cartQuantity = cartQuantity,
        commentAmount = commentAmount,
        orderQuantity = orderQuantity,
        depositPrice = depositPrice,
        isAvailable = isAvailable,
        isGift = isGift,
        replacementProductUIList = replacementProductEntityList.mapToUI(),
        detailPictureList = detailPictureList,
        pricePerUnitStringBuilder = StringBuilder()
            .append(pricePerUnit)
            .append(" ₽/кг")
            .toString(),
        currentPriceStringBuilder = getCurrentPrice(priceList.mapToUI(), isGift),
        oldPriceStringBuilder = getOldPrice(priceList.mapToUI(), isGift),
        minimalPriceStringBuilder = getMinimalPrice(priceList.mapToUI()),
        haveDiscount = checkHaveDiscount(priceList.mapToUI(), isGift)
    )

    private fun getCurrentPrice(list: List<PriceUI>, isGift: Boolean, isNegative: Boolean = false) : String {
        if (list.isEmpty()) return ""
        val price = list.first().currentPrice
        if (price  == 0 && isGift) {
            return "Подарок"
        }
        return StringBuilder()
            .append(when(isNegative && price != 0) {
                true -> "-"
                false -> ""
            })
            .append(price)
            .append(" ₽")
            .toString()
    }

    private fun getOldPrice(list: List<PriceUI>, isGift: Boolean, isNegative: Boolean = false) : String {
        if (list.isEmpty()) return ""
        val price = list.first().oldPrice
        if (price  == 0 && isGift) {
            return "Подарок"
        }
        return StringBuilder()
            .append(when(isNegative && price != 0) {
                true -> "-"
                false -> ""
            })
            .append(price)
            .append(" ₽")
            .toString()
    }

    private fun getMinimalPrice(list: List<PriceUI>) : String {
        if (list.isEmpty()) return ""
        val minimalPrice = list.maxByOrNull { it.requiredAmount }?.currentPrice!!
        return StringBuilder()
            .append("от ")
            .append(minimalPrice)
            .append(" ₽")
            .toString()
    }

    private fun checkHaveDiscount(list: List<PriceUI>, isGift: Boolean) : Boolean {
        return list.first().currentPrice < list.first().oldPrice || isGift
    }

}
