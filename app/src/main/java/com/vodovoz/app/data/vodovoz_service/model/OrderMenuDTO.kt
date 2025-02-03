package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class OrderMenuDTO(
    @Json(name = "MENU")
    val MENU: List<MENU_DTO?>?,
    @Json(name = "ZAKAZ")
    val ZAKAZ: ZAKAZ_DTO?
)