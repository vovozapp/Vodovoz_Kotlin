package com.vodovoz.app.data.vodovoz_service.model.user_data


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class UserDataDTO(
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "FOTO")
    val FOTO: FOTO_DTO?,
    @Json(name = "POLYA")
    val POLYA: List<USER_DATA_POLE_DTO>?
)