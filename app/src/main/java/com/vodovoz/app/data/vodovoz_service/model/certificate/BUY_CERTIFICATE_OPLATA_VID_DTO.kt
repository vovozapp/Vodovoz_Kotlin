package com.vodovoz.app.data.vodovoz_service.model.certificate


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BUY_CERTIFICATE_OPLATA_VID_DTO(
    @Json(name = "VALUE")
    val VALUE: Int?,
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "KARTINKA")
    val KARTINKA: String?,
    @Json(name = "TEXT")
    val TEXT: String?
)