package com.vodovoz.app.ui.fragment.home.adapter

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
}