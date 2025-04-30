package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.auth.KNOPKA_AUTH_DTO

@Keep
data class QuestionnairesDetailsDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "DATA")
    val DATA: List<QuestionnairesItemDTO>?,
    @Json(name = "KNOPKA")
    val KNOPKA: KNOPKA_AUTH_DTO?
)