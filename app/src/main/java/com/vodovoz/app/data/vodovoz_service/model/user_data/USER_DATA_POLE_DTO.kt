package com.vodovoz.app.data.vodovoz_service.model.user_data


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class USER_DATA_POLE_DTO(
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "OBYZATELNO")
    val OBYZATELNO: String?,
    @Json(name = "ZABLOCKPOLE")
    val ZABLOCKPOLE: String?,
    @Json(name = "POLE")
    val POLE: String?,
    @Json(name = "VALUE")
    val VALUE: String?,
    @Json(name = "TEXTOPIS")
    val TEXTOPIS: String?,
    @Json(name = "SPISOK")
    val SPISOK: List<SPISOK?>?
)