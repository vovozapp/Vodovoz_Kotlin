package com.vodovoz.app.data.vodovoz_service.model.catalog


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.BannerDTO

@Keep
data class CatalogDetailsDTO(
    @Json(name = "BANNER")
    val BANNER: List<BannerDTO?>?,
    @Json(name = "RAZDEL")
    val RAZDEL: List<CATALOG_CATEGORY_DTO?>?
)