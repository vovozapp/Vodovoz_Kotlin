package com.vodovoz.app.data.vodovoz_service.model

import com.squareup.moshi.Json

data class PreOrderDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "POLYA")
    val POLYA: List<FIELD_DTO>?,
    @Json(name = "KNOPKA")
    val KNOPKA: COLORFUL_KNOPKA_DTO?
)


