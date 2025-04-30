package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class QuestionnairesItemDTO(
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "TEXT_V_POLE")
    val TEXT_V_POLE: String?,
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "POLE")
    val POLE: String?,
    @Json(name = "OBYAZATELNO")
    val OBYAZATELNO: String?,
    @Json(name = "MULTIPLE")
    val MULTIPLE: String?,
    @Json(name = "VALUE")
    val VALUE: String?,
    @Json(name = "TIPPOLE")
    val TIPPOLE: Int?,
    @Json(name = "RAZDEL")
    val RAZDEL: List<String>?,
    @Json(name = "USLOVIE")
    val USLOVIE: List<USLOVIE_DTO>?
)

