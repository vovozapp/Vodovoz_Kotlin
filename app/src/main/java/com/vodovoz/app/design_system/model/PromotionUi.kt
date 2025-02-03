package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.AboutAdvertisingModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.PromotionSectionModel
import com.vodovoz.app.feature.home.model.LabelWithColorUi
import com.vodovoz.app.feature.home.model.mapToUi

@Immutable
data class PromotionUi(
    val id: Int,
    val picture: String,
    val label: LabelWithColorUi,
    val sectionId: Int,
    val blockId: Int,
    val timeLeft: String,
    val name: String,
    val aboutAdvertisingUi: AboutAdvertisingUi?,
)

@Immutable
data class AboutAdvertisingUi(
    val name: String,
    val title: String,
    val aboutCompanyTitle: String,
    val aboutCompany: String,
)

@Immutable
data class PromotionSectionUi(
    val id: Int,
    val code: String,
    val name: String,
) {

    companion object {
        val Empty = PromotionSectionUi(
            -1, "", ""
        )
    }

}


@JvmName("mapPromotionSectionListToUi")
fun List<PromotionSectionModel>.mapToUi(): List<PromotionSectionUi> {
    return map { it.mapToUi() }
}

fun PromotionSectionModel.mapToUi(): PromotionSectionUi {
    return PromotionSectionUi(
        id = id,
        code = code,
        name = name
    )
}


fun AboutAdvertisingModel.mapToUi(): AboutAdvertisingUi {
    return AboutAdvertisingUi(
        name = name,
        title = title,
        aboutCompanyTitle = aboutCompanyTitle,
        aboutCompany = aboutCompany
    )
}

fun PromotionModel.mapToUi(): PromotionUi {
    return PromotionUi(
        id = id,
        picture = detailPicture,
        label = label.mapToUi(),
        sectionId = sectionId,
        blockId = blockId,
        timeLeft = endDate, //todo - do format
        name = name,
        aboutAdvertisingUi = advertising?.mapToUi()
    )
}

fun List<PromotionModel>.mapToUi(): List<PromotionUi> {
    return map { it.mapToUi() }
}
