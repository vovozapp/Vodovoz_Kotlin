package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PopupWindowDTO(
    @Json(name = "BANNER")
    val BANNER: List<SPECTIAL_PROMOTION_DTO>?,
    @Json(name = "UPDATE")
    val UPDATE: APP_UPDATE_INFO_DTO?
)