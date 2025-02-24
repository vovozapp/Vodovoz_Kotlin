package com.vodovoz.app.data.vodovoz_service.model

import com.squareup.moshi.Json

class AnalogsSectionDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "SORTIROVKA")
    val SORTIROVKA: SORTIROVKA_DTO?,
    @Json(name = "TOVAR")
    val TOVAR: List<TOVAR_DATA_DTO>?,
)

class ProductsSectionDTO(
    @Json(name = "COUNT")
    val COUNT: Int? = null,
    @Json(name = "STRANIC")
    val STRANIC: Int? = null,
    @Json(name = "TITLE")
    val TITLE: String? = null,
    @Json(name = "SORTIROVKA")
    val SORTIROVKA: SORTIROVKA_DTO? = null,
    @Json(name = "RAZDEL")
    val RAZDEL: CategoriesDTO? = null,
    @Json(name = "DATA")
    val DATA: List<TOVAR_DATA_DTO>? = null,
    @Json(name = "__PODELITCA")
    val PODELITCA: PODELITCA_DTO? = null,
)

class CategoriesDTO(
    @Json(name = "LISTRAZDEL")
    val LISTRAZDEL: List<CATEGORY_DTO?>?,
    @Json(name = "TITLERAZDEL")
    val TITLERAZDEL: String?
)

class PODELITCA_DTO(
    @Json(name = "detail_page_url")
    val detailPageUrl: String? = null,
    @Json(name = "detail_page_url_ios")
    val detailPageUrlIOS: PAGE_URL_IOS_DTO? = null,
)

class PAGE_URL_IOS_DTO(
    @Json(name = "NAME")
    val name: String? = null,
    @Json(name = "URL")
    val url: String? = null,
)