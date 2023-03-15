package com.vodovoz.app.feature.home.adapter

import com.vodovoz.app.data.model.common.ActionEntity

interface HomeMainClickListener {

    //POSITION_1
    fun onBannerClick(actionEntity: ActionEntity?)

    //POSITION_16
    fun onAboutAppClick()
    fun onAboutDeliveryClick()
    fun onAboutPayClick()
    fun onAboutShopClick()
    fun onServicesClick()
    fun onContactsClick()
    fun onHowToOrderClick()

    //POSITION_12
    fun onBrandClick(id: Long)
    fun onShowAllBrandsClick()

    //POSITION_15
    fun onCommentsClick(id: Long?)
    fun onSendCommentAboutShop()

    //POSITION_13
    fun onCountryClick(id: Long)

    //POSITION_2
    fun onHistoryClick(id: Long)

    //POSITION_7
    fun onOrderClick(id: Long?)
    fun onShowAllOrdersClick()

    //POSITION_8
    fun onLastPurchasesClick()
    fun onOrdersHistoryClick()
    fun onShowAllFavoritesClick()

    //POSITION_3
    fun onCategoryClick(id: Long?)

    //POSITION_10
    fun onPromotionProductClick(id: Long)
    fun onNotifyWhenBeAvailable(id: Long, name: String, detailPicture: String)
    fun onChangeProductQuantity(id: Long, cartQuantity: Int)
    fun onFavoriteClick(id: Long, isFavorite: Boolean)
    fun onPromotionClick(id: Long)
    fun onShowAllPromotionsClick()

    //POSITION_4, POSITION_6, POSITION_9, POSITION_11, POSITION_14
    fun showAllDiscountProducts(id: Long)
    fun showAllTopProducts(id: Long)
    fun showAllNoveltiesProducts(id: Long)
    fun showAllBottomProducts(id: Long)

}