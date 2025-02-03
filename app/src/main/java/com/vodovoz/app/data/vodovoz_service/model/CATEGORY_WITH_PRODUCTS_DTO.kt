package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class CATEGORY_WITH_PRODUCTS_DTO(
    @Json(name = "data")
    val data: List<TOVAR_DATA_DTO>?,
    @Json(name = "ID")
    val ID: Long?,
    @Json(name = "NAME")
    val NAME: String?
)