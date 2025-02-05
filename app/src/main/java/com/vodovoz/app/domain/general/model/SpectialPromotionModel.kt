package com.vodovoz.app.domain.general.model

data class SpecialPromotionModel(
    val id: Int,
    val name: String,
    val text: String,
    val picture: String,
    val actionWithButton: ActionWithButtonModel,
)

data class AppUpdateInfoModel(
    val id: Int,
    val title: String,
    val text: String,
    val playMarketUrl: String,
    val androidVersion: String,
    val picture: String,
    val colorfulButton: ColorfulButtonModel,
)


data class PopupWindowInfoModel(
    val specialPromotion: SpecialPromotionModel?,
    val appUpdateInfo: AppUpdateInfoModel,
)