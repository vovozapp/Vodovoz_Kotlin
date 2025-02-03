package com.vodovoz.app.data.vodovoz_service.mappers

import com.vodovoz.app.data.vodovoz_service.di.toVodovozImage
import com.vodovoz.app.data.vodovoz_service.model.HIT_DTO
import com.vodovoz.app.data.vodovoz_service.model.OREKLAME_DTO
import com.vodovoz.app.data.vodovoz_service.model.PROMOTION_DATA_DTO
import com.vodovoz.app.data.vodovoz_service.model.PROMOTION_RAZDEL_DTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionsDTO
import com.vodovoz.app.domain.general.model.AboutAdvertisingModel
import com.vodovoz.app.domain.general.model.LabelModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.PromotionSectionModel
import com.vodovoz.app.domain.general.model.PromotionsWithSectionsModel
import com.vodovoz.app.domain.general.model.emptyLabelModel


fun PromotionsDTO.mapToDomain(): PromotionsWithSectionsModel {
    return PromotionsWithSectionsModel(
        title = TITLE ?: "",
        sections = RAZDELI?.filterNotNull()?.mapToDomain() ?: emptyList(),
        promotions = DATA?.mapToDomain() ?: emptyList()
    )

}

@JvmName("mapPromotionRazdelToDomain")
fun List<PROMOTION_RAZDEL_DTO>.mapToDomain(): List<PromotionSectionModel> {
    return mapNotNull { promotionRazdelDto ->
        promotionRazdelDto.mapToDomain()
    }
}

fun PROMOTION_RAZDEL_DTO.mapToDomain(): PromotionSectionModel? {
    return PromotionSectionModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        code = CODE ?: return null
    )
}

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

fun OREKLAME_DTO.mapToDomain(): AboutAdvertisingModel? {
    return AboutAdvertisingModel(
        name = this.NAME ?: "",
        title = this.ZAGOLOVOK ?: return null,
        aboutCompanyTitle = this.NAMEVNUTRI ?: return null,
        aboutCompany = this.DANNYE ?: return null
    )
}