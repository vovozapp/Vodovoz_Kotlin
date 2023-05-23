package com.vodovoz.app.feature.search.qrcode.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QrCodeModel(
    @Json(name = "data")
    val listData: List<QrCodeModelData>?
)

@JsonClass(generateAdapter = true)
data class QrCodeModelData(
    @Json(name = "ID")
    val id: String?
)
