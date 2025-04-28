package com.vodovoz.app.data.vodovoz_service.model.certificate


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.user_data.POLE_DTO

@Keep
data class BUY_CERTIFICATE_TAB_DTO(
    @Json(name = "TIP")
    val TIP: Int?,
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "DATA")
    val DATA: List<POLE_DTO>?
)