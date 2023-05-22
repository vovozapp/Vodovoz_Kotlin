package com.vodovoz.app.feature.productlist.adapter

interface ProductsClickListener {

    fun onProductClick(id: Long)
    fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String)
    fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int)
    fun onFavoriteClick(id: Long, isFavorite: Boolean)
    fun onChangeRating(id: Long, rating: Float, oldRating: Float)
    fun onChangeProductQuantityServiceDetails(id: Long, cartQuantity: Int, oldQuantity: Int, giftId: String) = Unit
}