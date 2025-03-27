package com.vodovoz.app.data.vodovoz_service.model.user_data


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class SPISOK(
    @Json(name = "NAME")
    val _VODNAME: String?,
    @Json(name = "ID")
    val _VODID: String?
)