package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PROFILE_MENO_OKNO_DTO(
    @Json(name = "ID")
    val ID: PROFILE_MENO_OKNO_ID_DTO?
)