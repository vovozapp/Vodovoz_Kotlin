package com.vodovoz.app.data.vodovoz_service.model.cart


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.NALICHIE_MORE_DTO

@Keep
data class KORZINA_PRODUCT_PODROBNO_DTO(
    @Json(name = "ACTIVE")
    val ACTIVE: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "IBLOCK_ID")
    val IBLOCK_ID: Int?,
    @Json(name = "ID")
    val ID: Long?,
    @Json(name = "DETAIL_PICTURE")
    val DETAIL_PICTURE: String?,
    @Json(name = "CATALOG_QUANTITY")
    val CATALOG_QUANTITY: Int?,
    @Json(name = "PROPERTY_ZALOG_VALUE")
    val PROPERTY_ZALOG_VALUE: String?,
    @Json(name = "PROPERTY_RENT_PRICE_VALUE")
    val PROPERTY_RENT_PRICE_VALUE: Any?,
    @Json(name = "ZAPRET_FISHKAM")
    val ZAPRET_FISHKAM: Int?,
    @Json(name = "CML2_ARTICLE")
    val CML2_ARTICLE: String?,
    //todo - maybe uncomment
//    @Json(name = "HIT")
//    val HITS: List<String>?,
//    @Json(name = "HIT")
//    val HIT: Boolean?,
    @Json(name = "FAVORITE")
    val FAVORITE: Boolean?,
    @Json(name = "NALICHIE_MORE")
    val NALICHIE_MORE: NALICHIE_MORE_DTO?
)