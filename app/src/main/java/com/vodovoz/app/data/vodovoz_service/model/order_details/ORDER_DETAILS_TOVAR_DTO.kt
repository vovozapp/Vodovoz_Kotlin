package com.vodovoz.app.data.vodovoz_service.model.order_details


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.EXTENDED_PRICE_DTO
import com.vodovoz.app.data.vodovoz_service.model.NALICHIE_MORE_DTO

@Keep
data class ORDER_DETAILS_TOVAR_DTO(
    @Json(name = "ID")
    val ID: Long?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "QUANTITY")
    val QUANTITY: String?,
    @Json(name = "PODAROK")
    val PODAROK: ORDER_PRODUCT_PODAROK_DTO?,
    @Json(name = "POKYPKA")
    val POKYPKA: String?,
    @Json(name = "CML2_ARTICLE")
    val CML2_ARTICLE: String?,
    @Json(name = "PROPERTY_ZALOG_VALUE")
    val PROPERTY_ZALOG_VALUE: String?,
    @Json(name = "ZAPRET_FISHKAM")
    val ZAPRET_FISHKAM: Int?,
    @Json(name = "EXTENDED_PRICE")
    val EXTENDED_PRICE: List<EXTENDED_PRICE_DTO?>?,
    @Json(name = "URL")
    val URL: Boolean?,
    //todo - put tovar18 class
    @Json(name = "TOVAR18")
    val TOVAR18: Any?,
    @Json(name = "FAVORITE")
    val FAVORITE: Boolean?,
    @Json(name = "PROPERTY_RATING_VALUE")
    val PROPERTY_RATING_VALUE: Double?,
    @Json(name = "DETAIL_PICTURE")
    val DETAIL_PICTURE: String?,
    @Json(name = "KOFFICIENT")
    val KOFFICIENT: Int?,
    @Json(name = "NALICHIE_MORE")
    val NALICHIE_MORE: List<NALICHIE_MORE_DTO>?,
    @Json(name = "EDINICAIZMERENIYA")
    val EDINICAIZMERENIYA: String?,
    @Json(name = "PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE")
    val PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE: Int?,
    @Json(name = "CATALOG_QUANTITY")
    val CATALOG_QUANTITY: Int?
)