package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toVodovozImage
import com.vodovoz.app.data.vodovoz_service.model.HIT_DTO
import com.vodovoz.app.data.vodovoz_service.model.OREKLAME_DTO
import com.vodovoz.app.data.vodovoz_service.model.PROMOTION_DATA_DTO
import com.vodovoz.app.domain.general.model.AboutAdvertising
import com.vodovoz.app.domain.general.model.LabelModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.emptyLabelModel

fun List<PROMOTION_DATA_DTO?>.mapToDomain(): List<PromotionModel> {
    return mapNotNull { promotionDataDto ->
        promotionDataDto?.mapToDomain()
    }
}

fun PROMOTION_DATA_DTO.mapToDomain(): PromotionModel? {
    return PromotionModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        blockId = IBLOCK_ID ?: -1,
        sectionId = IBLOCK_SECTION_ID ?: -1,
        detailPicture = DETAIL_PICTURE?.toVodovozImage() ?: return null,
        endDate = DATA_OUT ?: "",
        label = HIT?.mapToDomain() ?: emptyLabelModel(),
        advertising = OREKLAME?.mapToDomain()
    )
}

fun HIT_DTO.mapToDomain(): LabelModel {
    return LabelModel(
        name = TITLE ?: "",
        colorHex = BACKGROUND ?: ""
    )
}

fun OREKLAME_DTO.mapToDomain(): AboutAdvertising? {
    return AboutAdvertising(
        name = this.NAME ?: "",
        title = this.ZAGOLOVOK ?: return null,
        aboutCompanyTitle = this.NAMEVNUTRI ?: return null,
        aboutCompany = this.DANNYE ?: return null
    )
}