package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SuperTopAndBottomSectionsDTO(
    @Json(name = "RAZDEL_NIZ")
    val RAZDEL_NIZ: CATEGORY_RAZDEL?,
    @Json(name = "RAZDEL_VERH")
    val RAZDEL_VERH: CATEGORY_RAZDEL?
)