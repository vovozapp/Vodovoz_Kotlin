package com.vodovoz.app.data.vodovoz_service.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class OrderPlaceholderDTO(
    @Json(name = "TITLE") val title: String?,
    @Json(name = "ZAGALOVOK") val header: String?,
    @Json(name = "MESSAGE") val message: String?,
    @Json(name = "IMAGE") val imageUrl: String?,
    @Json(name = "KNOPKA") val button: OrderPlaceholderButton?,
)

@Keep
data class OrderPlaceholderButton(
    @Json(name = "TEXT")
    val text: String,
    @Json(name = "COLOR")
    val color: String,
    @Json(name = "BACKGROUND")
    val background: String,
    @Json(name = "OPLATA")
    val oplate: OPLATA_DTO,
)


