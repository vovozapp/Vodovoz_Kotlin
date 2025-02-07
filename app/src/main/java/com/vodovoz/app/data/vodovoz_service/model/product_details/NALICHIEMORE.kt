package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class NALICHIEMORE(
    @Json(name = "CVET")
    val T_CVET: String?,
    @Json(name = "NAME")
    val T_NAME: String?
)