package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class HARAKTERISTIK_BIND_DTO(
    @Json(name = "CODE")
    val CODE: String?,
    @Json(name = "HINT")
    val HINT: String?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "VALUE")
    val VALUE: String?
)