package com.vodovoz.app.feature.all

interface AllClickListener {

    fun onPromotionClick(id: Long) = Unit
    fun onBrandClick(id: Long) = Unit

    //Orders
    fun onMoreDetailClick(orderId: Long, sendReport: Boolean = false) = Unit
    fun onRepeatOrderClick(orderId: Long) = Unit
    fun onProductDetailPictureClick(productId: Long) = Unit
}