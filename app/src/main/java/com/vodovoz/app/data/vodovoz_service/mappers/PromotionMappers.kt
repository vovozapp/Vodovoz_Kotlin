package com.vodovoz.app.data.vodovoz_service.mappers

import android.text.Html
import com.vodovoz.app.data.vodovoz_service.di.toFullUrl
import com.vodovoz.app.data.vodovoz_service.model.AKCIYA_DTO
import com.vodovoz.app.data.vodovoz_service.model.HIT_DTO
import com.vodovoz.app.data.vodovoz_service.model.OREKLAME_DTO
import com.vodovoz.app.data.vodovoz_service.model.PROMOTION_DATA_DTO
import com.vodovoz.app.data.vodovoz_service.model.PROMOTION_RAZDEL_DTO
import com.vodovoz.app.data.vodovoz_service.model.PromotionsDTO
import com.vodovoz.app.domain.general.model.AboutAdvertisingModel
import com.vodovoz.app.domain.general.model.LabelModel
import com.vodovoz.app.domain.general.model.PromotionDetailsModel
import com.vodovoz.app.domain.general.model.PromotionCategoryModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.PromotionsSectionModel
import com.vodovoz.app.domain.general.model.emptyLabelModel

fun AKCIYA_DTO.toDomain(): PromotionDetailsModel? {
    return PromotionDetailsModel(
        id = ID ?: return null,
        picture = DETAIL_PICTURE?.toFullUrl() ?: return null,
        name = NAME ?: return null,
        description = DETAIL_TEXT ?: "",
        endDate = mapToZonedDateTime(DATAOUT ?: return null) ?: return null,
        advertising = OREKLAME?.toDomain() ?: return null,
        label = HIT?.toDomain()
    )
}

fun PromotionsDTO.toDomain(): PromotionsSectionModel {
    return PromotionsSectionModel(
        title = TITLE ?: "",
        categories = RAZDELI?.filterNotNull()?.toDomain() ?: emptyList(),
        promotions = DATA?.toDomain() ?: emptyList(),
        button = KNOPKA?.toDomain()
    )

}

@JvmName("mapPromotionRazdelToDomain")
fun List<PROMOTION_RAZDEL_DTO>.toDomain(): List<PromotionCategoryModel> {
    return mapNotNull { promotionRazdelDto ->
        promotionRazdelDto.toDomain()
    }
}

fun PROMOTION_RAZDEL_DTO.toDomain(): PromotionCategoryModel? {
    return PromotionCategoryModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        code = CODE ?: return null
    )
}

fun List<PROMOTION_DATA_DTO?>.toDomain(): List<PromotionModel> {
    return mapNotNull { promotionDataDto ->
        promotionDataDto?.toDomain()
    }
}


fun PROMOTION_DATA_DTO.toDomain(): PromotionModel? {
    return PromotionModel(
        id = ID ?: return null,
        name = NAME ?: return null,
        blockId = IBLOCK_ID ?: -1,
        sectionId = IBLOCK_SECTION_ID ?: -1,
        detailPicture = DETAIL_PICTURE?.toFullUrl() ?: return null,
        endDate = mapToZonedDateTime(DATA_OUT ?: return null) ?: return null,
        label = HIT?.toDomain() ?: emptyLabelModel(),
        advertising = OREKLAME?.toDomain()
    )
}

fun HIT_DTO.toDomain(): LabelModel? {
    return LabelModel(
        name = TITLE ?: return null,
        colorHex = BACKGROUND ?: return null
    )
}

fun OREKLAME_DTO.toDomain(): AboutAdvertisingModel? {
    val dannye = DANNYE ?: return null
    return AboutAdvertisingModel(
        name = this.NAME ?: "",
        title = this.ZAGOLOVOK ?: return null,
        aboutCompanyTitle = this.NAMEVNUTRI ?: return null,
        aboutCompany = Html.fromHtml(dannye).toString()
    )
}