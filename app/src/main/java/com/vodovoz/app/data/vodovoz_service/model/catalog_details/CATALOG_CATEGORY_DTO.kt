package com.vodovoz.app.data.vodovoz_service.model.catalog_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class CATALOG_CATEGORY_DTO(
    @Json(name = "DEPTH_LEVEL")
    val DEPTH_LEVEL: Int?,
    @Json(name = "IBLOCK_SECTION_ID")
    val IBLOCK_SECTION_ID: Int?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "PICTURE")
    val PICTURE: String?,
    @Json(name = "PODRAZDEL")
    val PODRAZDEL: List<CATALOG_CATEGORY_DTO?>?,
    @Json(name = "SUBSECTIONS")
    val SUBSECTIONS: Int?,
    @Json(name = "UF_SILKAPEREXOD")
    val UF_SILKAPEREXOD: String?
)