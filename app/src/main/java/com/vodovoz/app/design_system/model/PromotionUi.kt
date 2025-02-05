package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.AboutAdvertisingModel
import com.vodovoz.app.domain.general.model.PromotionDetailsModel
import com.vodovoz.app.domain.general.model.PromotionFilterModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.SpecialPromotionModel
import com.vodovoz.app.feature.home.model.LabelWithColorUi
import com.vodovoz.app.feature.home.model.toUi
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Locale


data class SpecialPromotionUi(
    val id: Int,
    val name: String,
    val text: String,
    val picture: String,
    val actionWithButton: ActionWithButtonUi,
) {

    companion object {
        val Empty = SpecialPromotionUi(-1, "", "", "", ActionWithButtonUi.Empty)
    }

}


fun SpecialPromotionModel.toDomain(): SpecialPromotionUi {
    return SpecialPromotionUi(
        id = id,
        name = name,
        text = text,
        picture = picture,
        actionWithButton = actionWithButton.toUi()
    )
}

@Immutable
data class PromotionDetailsUi(
    val id: Int,
    val picture: String,
    val name: String,
    val description: String,
    val timeLeft: String,
    val advertising: AboutAdvertisingUi,
    val label: LabelWithColorUi?,
) {
    companion object {
        val Empty = PromotionDetailsUi(
            id = -1,
            picture = "",
            name = "",
            description = "",
            timeLeft = "",
            advertising = AboutAdvertisingUi.Empty,
            label = null
        )
    }
}

fun PromotionDetailsModel.toUi(): PromotionDetailsUi {
    return PromotionDetailsUi(
        id = this.id,
        picture = this.picture,
        name = this.name,
        description = this.description,
        timeLeft = timeRemainingToEnd(endDate),
        advertising = this.advertising.toUi(),
        label = label?.toUi()
    )
}


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
        val Empty = AboutAdvertisingUi(
            name = "",
            title = "",
            aboutCompanyTitle = "",
            aboutCompany = ""
        )
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
fun List<PromotionFilterModel>.toUi(): List<PromotionSectionUi> {
    return map { it.toUi() }
}

fun PromotionFilterModel.toUi(): PromotionSectionUi {
    return PromotionSectionUi(
        id = id,
        code = code,
        name = name
    )
}


fun AboutAdvertisingModel.toUi(): AboutAdvertisingUi {
    return AboutAdvertisingUi(
        name = name,
        title = title,
        aboutCompanyTitle = aboutCompanyTitle,
        aboutCompany = aboutCompany
    )
}

fun PromotionModel.toUi(): PromotionUi {
    return PromotionUi(
        id = id,
        picture = detailPicture,
        label = label.toUi(),
        sectionId = sectionId,
        blockId = blockId,
        timeLeft = timeRemainingToEnd(endDate),
        name = name,
        aboutAdvertisingUi = advertising?.toUi()
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

fun List<PromotionModel>.toUi(): List<PromotionUi> {
    return map { it.toUi() }
}
