package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.AboutAdvertisingModel
import com.vodovoz.app.domain.general.model.PromotionDetailsModel
import com.vodovoz.app.domain.general.model.PromotionCategoryModel
import com.vodovoz.app.domain.general.model.PromotionModel
import com.vodovoz.app.domain.general.model.SpecialPromotionModel
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


fun SpecialPromotionModel.toUi(): SpecialPromotionUi {
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
    val advertising: AboutAdvertisingUi?,
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
        advertising = this.advertising?.toUi(),
        label = label?.toUi()
    )
}


@Immutable
data class PromotionUi(
    val id: Long,
    val picture: String,
    val label: LabelWithColorUi?,
    val categoryId: Int,
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
data class PromotionCategoryUi(
    val id: Int,
    val code: String,
    val name: String,
) {

    companion object {
        val Empty = PromotionCategoryUi(
            -1, "", ""
        )
    }

}


@JvmName("mapPromotionSectionListToDomain")
fun List<PromotionCategoryUi>.mapToDomain(): List<PromotionCategoryModel> {
    return map { promotionCategoryUi -> promotionCategoryUi.toDomain() }
}

fun PromotionCategoryUi.toDomain(): PromotionCategoryModel {
    return PromotionCategoryModel(
        id = id,
        code = code,
        name = name
    )
}


@JvmName("mapPromotionSectionListToUi")
fun List<PromotionCategoryModel>.mapToUi(): List<PromotionCategoryUi> {
    return map { it.toUi() }
}

fun PromotionCategoryModel.toUi(): PromotionCategoryUi {
    return PromotionCategoryUi(
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
        categoryId = sectionId,
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
