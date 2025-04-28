package com.vodovoz.app.data.vodovoz_service.model.certificate


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class BUY_CERTIFICATE_OPLATA_DTO(
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "OBAZATELEN")
    val OBAZATELEN: String?,
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "DATA")
    val DATA: List<BUY_CERTIFICATE_OPLATA_VID_DTO>?
)