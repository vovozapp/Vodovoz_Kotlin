package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class POPULAR_CATEGORY_DTO(
    @Json(name = "IDRAZDEL")
    val IDRAZDEL: Int?,
    @Json(name = "NAMERAZDEL")
    val NAMERAZDEL: String?,
    @Json(name = "PICTURE")
    val PICTURE: String?
)

@Keep
data class CATEGORY_DTO(
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?,
)
