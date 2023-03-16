package com.vodovoz.app.feature.home.viewholders.homeproducts.inneradapter.inneradapterproducts

interface ProductsClickListener {

    fun onProductClick(id: Long)
    fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String)
    fun onChangeProductQuantity(id: Long, cartQuantity: Int)
    fun onFavoriteClick(id: Long, isFavorite: Boolean)
}