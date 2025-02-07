package com.vodovoz.app.data.vodovoz_service.model.product_details


import com.squareup.moshi.Json
import androidx.annotation.Keep
import com.vodovoz.app.data.vodovoz_service.model.TOVAR_DATA_DTO

@Keep
data class PRODUCT_DETAILS_SECTION_DTO(
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "REKOMEND")
    val REKOMEND: List<TOVAR_DATA_DTO?>?
)