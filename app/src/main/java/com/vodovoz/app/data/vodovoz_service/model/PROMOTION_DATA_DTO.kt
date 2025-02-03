package com.vodovoz.app.data.vodovoz_service.model


import com.squareup.moshi.Json
import androidx.annotation.Keep

@Keep
data class PROMOTION_DATA_DTO(
    @Json(name = "DATAOUT")
    val DATA_OUT: String?,
    @Json(name = "DETAIL_PICTURE")
    val DETAIL_PICTURE: String?,
    @Json(name = "HIT")
    val HIT: HIT_DTO?,
    @Json(name = "IBLOCK_ID")
    val IBLOCK_ID: Int?,
    @Json(name = "IBLOCK_SECTION_ID")
    val IBLOCK_SECTION_ID: Int?,
    @Json(name = "ID")
    val ID: Int?,
    @Json(name = "NAME")
    val NAME: String?,
    @Json(name = "OREKLAME")
    val OREKLAME: OREKLAME_DTO?
)