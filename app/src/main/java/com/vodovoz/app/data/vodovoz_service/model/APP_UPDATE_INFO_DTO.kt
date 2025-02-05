package com.vodovoz.app.data.vodovoz_service.model


import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class APP_UPDATE_INFO_DTO(
    @Json(name = "HARAKTERISTIK")
    val HARAKTERISTIK: UPDATE_HARAKTERISTIK_DTO?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "KARTINKA")
    val KARTINKA: String?,
    @Json(name = "SILKA_ANDROID")
    val SILKA_ANDROID: String?,
    @Json(name = "SILKA_IPHONE")
    val SILKA_IPHONE: String?,
    @Json(name = "TEXT")
    val TEXT: String?,
    @Json(name = "TITLE")
    val TITLE: String?,
    @Json(name = "VERSIYA_ANDROID")
    val VERSIYA_ANDROID: String?,
    @Json(name = "VERSIYA_IPHONE")
    val VERSIYA_IPHONE: String?,
)