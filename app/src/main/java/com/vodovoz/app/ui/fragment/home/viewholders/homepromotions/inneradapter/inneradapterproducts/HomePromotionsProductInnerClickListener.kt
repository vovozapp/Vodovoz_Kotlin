package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter.inneradapterproducts

interface HomePromotionsProductInnerClickListener {

    fun onPromotionProductClick(id: Long)
    fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String)
    fun onChangeProductQuantity(id: Long, cartQuantity: Int)
    fun onFavoriteClick(id: Long, isFavorite: Boolean)
}