package com.vodovoz.app.data.vodovoz_service.model.profile


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class CHAT_MENU_DTO(
    @Json(name = "IMAGE")
    val IMAGE: String?,
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "ID")
    val ID: String?,
    @Json(name = "CHATDAN")
    val CHATDAN: String?,
    @Json(name = "CHATDANIOS")
    val CHATDANIOS: String?
)