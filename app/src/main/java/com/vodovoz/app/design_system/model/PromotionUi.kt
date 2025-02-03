package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.AboutAdvertisingModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.PromotionSectionModel
import com.vodovoz.app.feature.home.model.LabelWithColorUi
import com.vodovoz.app.feature.home.model.mapToUi
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Locale

@Immutable
data class PromotionUi(
    val id: Int,
    val picture: String,
    val label: LabelWithColorUi?,
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
) {
    companion object {
        val Empty = AboutAdvertisingUi("", "", "", "")
    }

}

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
        timeLeft = timeRemainingToEnd(endDate),
        name = name,
        aboutAdvertisingUi = advertising?.mapToUi()
    )
}

fun timeRemainingToEnd(endDateTime: ZonedDateTime): String {
    val now = ZonedDateTime.now()
    val duration = Duration.between(now, endDateTime)

    val days = duration.toDays()
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60

    return String.format(Locale.getDefault(), "%02dд : %dч : %02dм", days, hours, minutes)
}

fun List<PromotionModel>.mapToUi(): List<PromotionUi> {
    return map { it.mapToUi() }
}
