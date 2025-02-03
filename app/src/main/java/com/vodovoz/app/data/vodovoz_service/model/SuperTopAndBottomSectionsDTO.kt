package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SuperTopAndBottomSectionsDTO(
    @Json(name = "RAZDEL_NIZ")
    val RAZDEL_NIZ: RAZDEL_VERH_NIH?,
    @Json(name = "RAZDEL_VERH")
    val RAZDEL_VERH: RAZDEL_VERH_NIH?
)