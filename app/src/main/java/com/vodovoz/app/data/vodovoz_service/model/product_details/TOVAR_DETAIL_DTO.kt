package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.EXTENDED_PRICE_DTO
import com.vodovoz.app.data.vodovoz_service.model.NALICHIE_MORE_DTO

@Keep
data class TOVAR_DETAIL_DTO(
    @Json(name = "ACTIVE")
    val ACTIVE: String?,
    @Json(name = "BAR_CODE")
    val BAR_CODE: String?,
    @Json(name = "BLOCKRAZDEL")
    val BLOCKRAZDEL: BLOCK_RAZDEL_DTO?,
    @Json(name = "DETAIL_PAGE_URL")
    val DETAIL_PAGE_PATH: String?,
    @Json(name = "DETAIL_PICTURE")
    val DETAIL_PICTURE: String?,
    @Json(name = "DETAIL_TEXT")
    val DETAIL_TEXT: TOVAR_DETAIL_TEXT_DTO?,
    @Json(name = "DOCUMENTS")
    val DOCUMENTS: DOCUMENTS_DTO?,
    @Json(name = "DOPKNOPKI")
    val DOPKNOPKI: DOPKNOPKI_DTO?,
    @Json(name = "DOPTSENA_ZA_EDINICY")
    val DOPTSENA_ZA_EDINICY: String?,
    @Json(name = "EDINICAIZMERENIYA")
    val EDINICAIZMERENIYA: String?,
    @Json(name = "EXTENDED_PRICE")
    val EXTENDEDPRICE: List<EXTENDED_PRICE_DTO>?,
    @Json(name = "FAVORITE")
    val FAVORITE: Boolean?,
    @Json(name = "HARAKTERISTIKI")
    val HARAKTERISTIKI: PRODUCT_DETAIL_HARAKTERISTIKI?,
    @Json(name = "ID")
    val ID: Long?,
    @Json(name = "INFORMATIONS")
    val INFORMATIONS: INFORMATIONS_DTO?,
    @Json(name = "KOFFICIENT")
    val KOFFICIENT: Float?,
    @Json(name = "KOLLTOVAR")
    val KOLLTOVAR: Int?,
    @Json(name = "MORE_PHOTO")
    val MORE_PHOTO: List<String>?,
    @Json(name = "NALICHIE")
    val NALICHIE: List<NALICHIE_MORE_DTO>?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "PROPERTY_RATING_VALUE")
    val PROPERTY_RATING_VALUE: Double?,
    @Json(name = "RUTUBE_VIDEO")
    val RUTUBE_VIDEO: List<RUTUBEVIDEO_DTO>?,
    @Json(name = "YOUTUBE_VIDEO")
    val YOUTUBE_VIDEO: List<RUTUBEVIDEO_DTO>?,
    @Json(name = "TAGS")
    val TAGS: TAGS_DTO?,
    @Json(name = "ZALOG")
    val ZALOG: ZALOG_DTO?
)