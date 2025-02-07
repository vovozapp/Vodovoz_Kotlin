package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class ProductDetailsDTO(
    @Json(name = "BLOCTOVAR")
    val BLOCTOVAR: BLOCTOVAR_DTO?,
    @Json(name = "COMMENTS")
    val COMMENTS: COMMENTS_DTO?,
    @Json(name = "DETAILTEXT")
    val DETAILTEXT: List<DETAILTEXT_DTO>?,
    @Json(name = "KNOPKI")
    val KNOPKI: PRODUCT_DETAILS_KNOPKI_DTO?,
    @Json(name = "PODILITSYA")
    val PODILITSYA: PODILITSYA_DTO?,
    @Json(name = "TOVAR")
    val TOVAR: TOVAR_DETAIL_DTO?
)