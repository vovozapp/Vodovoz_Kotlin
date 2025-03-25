package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PROFILE_MENU_DTO(
    @Json(name = "MINI")
    val MINI: List<PROFILE_MINI_MENU_DTO>?,
    @Json(name = "NORMAL")
    val NORMAL: List<PROFILE_NORMAL_MENU_DTO>?
)