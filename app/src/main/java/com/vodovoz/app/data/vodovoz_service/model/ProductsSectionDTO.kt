package com.vodovoz.app.data.vodovoz_service.model

import com.squareup.moshi.Json

class ProductsSectionDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "SORTIROVKA")
    val SORTIROVKA: SORTIROVKA_DTO?,
    @Json(name = "TOVAR")
    val TOVAR: List<TOVAR_DATA_DTO>?,
)