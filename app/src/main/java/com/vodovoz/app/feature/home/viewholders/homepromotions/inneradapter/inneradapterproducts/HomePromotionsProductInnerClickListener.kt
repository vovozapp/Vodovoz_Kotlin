package com.vodovoz.app.feature.home.viewholders.homepromotions.inneradapter.inneradapterproducts

interface HomePromotionsProductInnerClickListener {

    fun onPromotionProductClick(id: Long)
    fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String)
    fun onChangeProductQuantity(id: Long, cartQuantity: Int, oldQuantity: Int)
    fun onFavoriteClick(id: Long, isFavorite: Boolean)
}