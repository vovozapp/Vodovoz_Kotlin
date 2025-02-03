package com.vodovoz.app.domain.general.model


data class PromotionsWithSectionsModel(
    val title: String,
    val sections: List<PromotionSectionModel>,
    val promotions: List<PromotionModel>,
)


data class PromotionSectionModel(
    val id: Int,
    val name: String,
    val code: String,
)

data class PromotionModel(
    val id: Int,
    val name: String,
    val blockId: Int,
    val sectionId: Int,
    val detailPicture: String,
    val endDate: String,
    val label: LabelModel,
    val advertising: AboutAdvertisingModel?,
)

data class LabelModel(
    val name: String,
    val colorHex: String,
)

fun emptyLabelModel() = LabelModel("", "")

data class AboutAdvertisingModel(
    val name: String,
    val title: String,
    val aboutCompanyTitle: String,
    val aboutCompany: String,
)


