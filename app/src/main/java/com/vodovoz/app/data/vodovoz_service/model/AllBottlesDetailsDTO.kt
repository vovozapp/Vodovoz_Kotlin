package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class AllBottlesDetailsDTO(
    @Json(name = "OPISANIE")
    val OPISANIE: String?,
    @Json(name = "KPOPKAPLUS")
    val KPOPKAPLUS: String?,
    @Json(name = "TARA")
    val TARA: List<TARA_DTO>?
)