package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PopularCategoriesDTO(
    @Json(name = "LISTRAZDEL")
    val LISTRAZDEL: List<POPULAR_CATEGORY_DTO?>?,
    @Json(name = "TITLERAZDEL")
    val TITLERAZDEL: String?
)