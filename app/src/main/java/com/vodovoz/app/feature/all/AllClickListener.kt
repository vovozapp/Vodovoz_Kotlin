package com.vodovoz.app.feature.all

import com.vodovoz.app.feature.home.viewholders.homepromotions.model.PromotionAdvEntity

interface AllClickListener {

    fun onPromotionClick(id: Long) = Unit
    fun onPromotionAdvClick(promotionAdvEntity: PromotionAdvEntity?) {}
    fun onBrandClick(id: Long) = Unit

    //Orders
    fun onMoreDetailClick(orderId: Long, sendReport: Boolean = false) = Unit
    fun onRepeatOrderClick(orderId: Long) = Unit
    fun onProductDetailPictureClick(productId: Long) = Unit
}