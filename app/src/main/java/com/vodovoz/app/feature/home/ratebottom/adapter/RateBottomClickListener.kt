package com.vodovoz.app.feature.home.ratebottom.adapter

interface RateBottomClickListener {

    fun dontCommentProduct(id: Long)
    fun rateProduct(id: Long, ratingCount: Int)
}