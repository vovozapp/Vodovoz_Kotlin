package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.user_data.POLE_DTO

@Keep
data class CancelOrderDetailsDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "OPISANIE")
    val OPISANIE: String?,
    @Json(name = "DOPOPISANIE")
    val DOPOPISANIE: String?,
    @Json(name = "STATYS")
    val STATYS: CHECKBOXES_DTO?,
    @Json(name = "SOOBSHENIE")
    val SOOBSHENIE: POLE_DTO?,
    @Json(name = "KNOPKA")
    val KNOPKA: KNOPKA_ORDER_DTO?
)