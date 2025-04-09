package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class AnalogsSectionDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "SORTIROVKA")
    val SORTIROVKA: SORTIROVKA_DTO?,
    @Json(name = "TOVAR")
    val TOVAR: List<TOVAR_DATA_DTO>?,
)

@Keep
data class ProductsSectionDTO(
    @Json(name = "COUNT")
    val COUNT: String? = null,
    @Json(name = "TOVARVSEGO")
    val TOVARVSEGO: String? = null,
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
    @Json(name = "TOVAR")
    val TOVAR: List<TOVAR_DATA_DTO>? = null,
    @Json(name = "PODELITCA")
    val PODELITCA: PODELITCA_DTO? = null,
)

@Keep
class CategoriesDTO(
    @Json(name = "LISTRAZDEL")
    val LISTRAZDEL: List<CATEGORY_DTO?>?,
    @Json(name = "TITLERAZDEL")
    val TITLERAZDEL: String?,
)

@Keep
class PODELITCA_DTO(
    @Json(name = "detail_page_url")
    val detailPageUrl: String? = null,
    @Json(name = "detail_page_url_ios")
    val detailPageUrlIOS: PAGE_URL_IOS_DTO? = null,
)

@Keep
class PAGE_URL_IOS_DTO(
    @Json(name = "NAME")
    val name: String? = null,
    @Json(name = "URL")
    val url: String? = null,
)