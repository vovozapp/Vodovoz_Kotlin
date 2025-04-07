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
    @Json(name = "UF_SILKAPEREXOD")
    val UF_SILKAPEREXOD: String?
)