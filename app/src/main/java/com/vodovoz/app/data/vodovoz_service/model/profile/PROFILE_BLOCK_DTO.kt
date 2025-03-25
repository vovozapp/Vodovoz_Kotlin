package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PROFILE_BLOCK_DTO(
    @Json(name = "ZAGALOVOK")
    val ZAGALOVOK: ZAGALOVOK_DTO?,
    @Json(name = "OPISANIE")
    val OPISANIE: OPISANIE_DTO?,
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "ID")
    val ID: String?
)