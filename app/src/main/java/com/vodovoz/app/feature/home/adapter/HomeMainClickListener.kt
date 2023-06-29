package com.vodovoz.app.feature.home.adapter

import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.ui.model.CommentUI

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
    fun onCommentsClick(item: CommentUI)
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

}