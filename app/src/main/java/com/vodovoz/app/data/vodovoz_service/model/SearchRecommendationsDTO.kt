package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.vodovoz.app.data.vodovoz_service.model.product_details.TOVAR_SECTION_DTO

@Keep
data class SearchRecommendationsDTO(
    @Json(name = "SLOVA")
    val SLOVA: List<String>?,
    @Json(name = "REKOMEND")
    val REKOMEND: TOVAR_SECTION_DTO?
)
