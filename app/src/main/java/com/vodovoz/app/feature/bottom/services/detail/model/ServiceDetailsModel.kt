package com.vodovoz.app.feature.bottom.services.detail.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceDetailsModel(
    @Json(name = "data")
    val serviceDetailsData: ServiceDetailsData?,
    val message: String?,
    val status: String?
)

@JsonClass(generateAdapter = true)
data class ServiceDetailsData(
    val DETAIL_TEXT: String?,
    val ID: String?,
    val NAME: String?,
    val PREVIEW_PICTURE: String?,
    val TOVARY: List<TOVARY>?
)

@JsonClass(generateAdapter = true)
data class TOVARY(
    val DOPTOVAR: String?,
    val IDKNOPKI: String?,
    val KOEFFICIENT: Int?,
    val TOVARY: List<TOVARYX>?
)

@JsonClass(generateAdapter = true)
data class NewPrice(
    val DescPrice: String?,
    val Price: String?
)

@JsonClass(generateAdapter = true)
data class MOREPHOTO(
    val VALUE: List<String>?
)

@JsonClass(generateAdapter = true)
data class EXTENDEDPRICE(
    val OLD_PRICE: Int?,
    val PRICE: Int?,
    val QUANTITY_FROM: Int?,
    val QUANTITY_TO: Int?
)

@JsonClass(generateAdapter = true)
data class TOVARYX(
    val CATALOG_AVAILABLE: String?,
    val CATALOG_BUNDLE: String?,
    val CATALOG_CAN_BUY_ZERO: String?,
    val CATALOG_CAN_BUY_ZERO_ORIG: String?,
    val CATALOG_HEIGHT: Any?,
    val CATALOG_LENGTH: Any?,
    val CATALOG_MEASURE: String?,
    val CATALOG_NEGATIVE_AMOUNT_TRACE: String?,
    val CATALOG_NEGATIVE_AMOUNT_TRACE_ORIG: String?,
    val CATALOG_PRICE_TYPE: String?,
    val CATALOG_PURCHASING_CURRENCY: String?,
    val CATALOG_PURCHASING_PRICE: String?,
    val CATALOG_QUANTITY: Int?,
    val CATALOG_QUANTITY_RESERVED: String?,
    val CATALOG_QUANTITY_TRACE: String?,
    val CATALOG_QUANTITY_TRACE_ORIG: String?,
    val CATALOG_RECUR_SCHEME_LENGTH: Any?,
    val CATALOG_RECUR_SCHEME_TYPE: String?,
    val CATALOG_SELECT_BEST_PRICE: String?,
    val CATALOG_SUBSCRIBE: String?,
    val CATALOG_SUBSCRIBE_ORIG: String?,
    val CATALOG_TRIAL_PRICE_ID: Any?,
    val CATALOG_TYPE: String?,
    val CATALOG_VAT: String?,
    val CATALOG_VAT_ID: String?,
    val CATALOG_VAT_INCLUDED: String?,
    val CATALOG_WEIGHT: String?,
    val CATALOG_WIDTH: Any?,
    val CATALOG_WITHOUT_ORDER: String?,
    val COUTCOMMENTS: String?,
    val DATE_ACTIVE_FROM: Any?,
    val DETAIL_PICTURE: String?,
    val EDINICAIZMERENIYA: Any?,
    val EXTENDED_PRICE: List<EXTENDEDPRICE>?,
    val FAVORITE: Boolean?,
    val IBLOCK_ID: String?,
    val ID: String?,
    val KOFFICIENT: Int?,
    val MORE_PHOTO: MOREPHOTO?,
    val NALICHIE: Any?,
    val NAME: String?,
    val NewPrice: NewPrice?,
    val PROPERTY_NEWPRODUCT_ENUM_ID: Any?,
    val PROPERTY_NEWPRODUCT_VALUE: Any?,
    val PROPERTY_NEWPRODUCT_VALUE_ID: Any?,
    val PROPERTY_PODAROK_ENUM_ID: Any?,
    val PROPERTY_PODAROK_VALUE: Any?,
    val PROPERTY_PODAROK_VALUE_ID: Any?,
    val PROPERTY_RATING_VALUE: Double?,
    val PROPERTY_RATING_VALUE_ID: String?,
    val PROPERTY_SALELEADER_ENUM_ID: Any?,
    val PROPERTY_SALELEADER_VALUE: Any?,
    val PROPERTY_SALELEADER_VALUE_ID: Any?,
    val PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE: Int?,
    val PROPERTY_TSENA_ZA_EDINITSU_TOVARA_VALUE_ID: String?,
    val PROPERTY_ZALOG_VALUE: Int?,
    val SORT: String?
)