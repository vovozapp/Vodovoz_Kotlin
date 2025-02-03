package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class OREKLAME_DTO(
    @Json(name = "DANNYE")
    val DANNYE: String?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "NAMEVNUTRI")
    val NAMEVNUTRI: String?,
    @Json(name = "ZAGOLOVOK")
    val ZAGOLOVOK: String?
)