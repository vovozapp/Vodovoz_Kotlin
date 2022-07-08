package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.ProductEntity
import com.vodovoz.app.ui.model.ProductUI

object ProductMapper {

    fun List<ProductEntity>.mapToUI(): List<ProductUI> = mutableListOf<ProductUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun ProductEntity.mapToUI() = ProductUI(
        id = id,
        name = name,
        detailPicture = detailPicture,
        isFavorite = isFavorite,
        oldPrice = oldPrice,
        newPrice = newPrice,
        status = status,
        statusColor = statusColor,
        rating = rating,
        cartQuantity = cartQuantity,
        commentAmount = commentAmount,
        orderQuantity = orderQuantity,
        isCanReturnBottles = isCanReturnBottles,
        detailPictureList = detailPictureList
    )

}
