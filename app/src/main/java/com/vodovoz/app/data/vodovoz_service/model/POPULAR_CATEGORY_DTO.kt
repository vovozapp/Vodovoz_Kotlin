package com.vodovoz.app.data.vodovoz_service.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class POPULAR_CATEGORY_DTO(
    @Json(name = "IDRAZDEL")
    val IDRAZDEL: Long?,
    @Json(name = "NAMERAZDEL")
    val NAMERAZDEL: String?,
    @Json(name = "PICTURE")
    val PICTURE: String?,
)

@Keep
data class CATEGORY_DTO(
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "DEPTH_LEVEL")
    val DEPTH_LEVEL: Int?,
)
