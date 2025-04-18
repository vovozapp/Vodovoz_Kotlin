package com.vodovoz.app.domain.general.model

import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.design_system.model.SectionUi
import com.vodovoz.app.design_system.model.toUi
import java.time.ZonedDateTime


data class PromotionsSectionModel(
    val title: String,
    val categories: List<PromotionCategoryModel>,
    val promotions: List<PromotionModel>,
    val button: ButtonModel?
)


fun PromotionsSectionModel.toUi(): SectionUi<PromotionUi> {
    return SectionUi(
        title = title,
        items = promotions.map { promotionModel -> promotionModel.toUi() },
        button = button?.toUi()
    )
}



data class PromotionCategoryModel(
    val id: Int,
    val name: String,
    val code: String,
)

data class PromotionModel(
    val id: Long,
    val name: String,
    val blockId: Int,
    val sectionId: Int,
    val detailPicture: String,
    val endDate: ZonedDateTime,
    val label: LabelModel,
    val advertising: AboutAdvertisingModel?,
)

@JvmInline
value class ProductsTitle(
    val title: String,
)

data class PromotionDetailsModel(
    val id: Int,
    val picture: String,
    val name: String,
    val description: String,
    val endDate: ZonedDateTime,
    val advertising: AboutAdvertisingModel?,
    val label: LabelModel?,
)

data class LabelModel(
    val name: String,
    val colorHex: String,
    val backgroundHex: String = ""
)

fun emptyLabelModel() = LabelModel("", "")

data class AboutAdvertisingModel(
    val name: String,
    val title: String,
    val aboutCompanyTitle: String,
    val aboutCompany: String,
)


