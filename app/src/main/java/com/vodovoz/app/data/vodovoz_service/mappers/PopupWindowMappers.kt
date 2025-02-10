package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.APP_UPDATE_INFO_DTO
import com.vodovoz.app.data.vodovoz_service.model.PopupWindowDTO
import com.vodovoz.app.data.vodovoz_service.model.SPECTIAL_PROMOTION_DTO
import com.vodovoz.app.domain.general.model.AppUpdateInfoModel
import com.vodovoz.app.domain.general.model.PopupWindowInfoModel
import com.vodovoz.app.domain.general.model.SpecialPromotionModel

fun PopupWindowDTO.toDomain(): PopupWindowInfoModel {
    return PopupWindowInfoModel(
        specialPromotion = BANNER?.firstOrNull()?.toDomain(),
        appUpdateInfo = UPDATE!!.toDomain()
    )
}


fun SPECTIAL_PROMOTION_DTO.toDomain(): SpecialPromotionModel? {
    return SpecialPromotionModel(
        id = ID ?: -1,
        name = NAME ?: "",
        text = TEXT ?: "",
        picture = KARTINKA?.toFullUrl() ?: return null,
        actionWithButton = HARAKTERISTIK?.toDomain() ?: return null
    )
}

fun APP_UPDATE_INFO_DTO.toDomain(): AppUpdateInfoModel {
    return AppUpdateInfoModel(
        id = ID ?: -1,
        title = TITLE ?: "",
        text = TEXT ?: "",
        playMarketUrl = SILKA_ANDROID ?: "",
        androidVersion = VERSIYA_ANDROID.toString(),
        picture = KARTINKA?.toFullUrl() ?: "",
        colorfulButton = HARAKTERISTIK?.KNOPKA?.toDomain()!!
    )
}