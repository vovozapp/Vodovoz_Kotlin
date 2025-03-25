package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PROFIL_DTO(
    @Json(name = "FIO")
    val FIO: String?,
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "TEXT_KNOPKA")
    val TEXT_KNOPKA: TEXT_KNOPKA_DTO?
)