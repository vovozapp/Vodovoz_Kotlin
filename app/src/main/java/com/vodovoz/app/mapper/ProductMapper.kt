package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.detail.DetailPicturePager
import com.vodovoz.app.mapper.PriceMapper.mapToUI
import com.vodovoz.app.ui.model.PriceUI
import com.vodovoz.app.ui.model.ProductUI
import kotlin.math.roundToInt

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
        labels = labels,
        rating = rating.toFloat(),
        cartQuantity = cartQuantity,
        commentAmount = commentAmount,
        orderQuantity = orderQuantity,
        depositPrice = depositPrice,
        isAvailable = isAvailable,
        isGift = isGift,
        replacementProductUIList = replacementProductEntityList.mapToUI(),
        detailPictureList = detailPictureList,
        currentPriceStringBuilder = getCurrentPrice(priceList.mapToUI(), isGift),
        oldPriceStringBuilder = getOldPrice(priceList.mapToUI(), isGift),
        minimalPriceStringBuilder = getMinimalPrice(priceList.mapToUI()),
        haveDiscount = checkHaveDiscount(priceList.mapToUI(), isGift),
        priceConditionStringBuilder = getPriceCondition(priceList.mapToUI()),
        discountPercentStringBuilder = getDiscountPercent(priceList.mapToUI()),
        detailPictureListPager = detailPictureList.map { DetailPicturePager(it) },
        chipsBan = chipsBan,
        totalDisc = totalDisc,
        conditionPrice = conditionalPrice,
        condition = condition,
        canBuy = canBuy,
        forCart = forCart,
        giftText = giftText,
    )

//    private fun getPricePerUnitStringBuilder(pricePerUnit: Int): String {
//        return if (pricePerUnit == 0) {
//            ""
//        } else {
//            StringBuilder()
//                .append(pricePerUnit)
//                .append(" ₽/кг")
//                .toString()
//        }
//    }

    private fun getCurrentPrice(
        list: List<PriceUI>,
        isGift: Boolean,
        isNegative: Boolean = false,
    ): String {
        if (list.isEmpty()) return ""
        val price = list.first().currentPrice
        if (price == 0.0 && isGift) {
            return "Подарок"
        }
        return StringBuilder()
            .append(
                when (isNegative && price != 0.0) {
                    true -> "-"
                    false -> ""
                }
            )
            .append(price.roundToInt())
            .append(" ₽")
            .toString()
    }

    private fun getOldPrice(
        list: List<PriceUI>,
        isGift: Boolean,
        isNegative: Boolean = false,
    ): String {
        if (list.isEmpty()) return ""
        val price = list.first().oldPrice
        if (price == 0.0 && isGift) {
            return "Подарок"
        }
        val intPrice = price.roundToInt()
        return if(intPrice != 0) {
            StringBuilder()
                .append(
                    when (isNegative) {
                        true -> "-"
                        false -> ""
                    }
                )
                .append(intPrice)
                .append(" ₽")
                .toString()
        } else { "" }
    }

    private fun getMinimalPrice(list: List<PriceUI>): String {
        if (list.isEmpty()) return ""
        val minimalPrice = list.maxByOrNull { it.requiredAmount }?.currentPrice!!
        return StringBuilder()
            .append("от ")
            .append(minimalPrice.roundToInt())
            .append(" ₽")
            .toString()
    }

    private fun getPriceCondition(list: List<PriceUI>): String {
        if (list.isEmpty()) return ""
        val minimalPrice = list.maxByOrNull { it.requiredAmount }?.requiredAmount!!
        return StringBuilder()
            .append("При условии покупки от ")
            .append(minimalPrice)
            .append(" шт")
            .toString()
    }

    private fun getDiscountPercent(list: List<PriceUI>): String {
        if (list.isEmpty()) return ""
        val newPrice = list.first().currentPrice
        val oldPrice = list.first().oldPrice
        if (oldPrice == 0.0) return ""

        return StringBuilder()
            .append((100.0 - ((newPrice / oldPrice) * 100)).roundToInt())
            .append("%")
            .toString()
    }

    private fun checkHaveDiscount(list: List<PriceUI>, isGift: Boolean): Boolean {
        if (list.isEmpty()) return false
        return list.first().currentPrice < list.first().oldPrice || isGift
    }

}
