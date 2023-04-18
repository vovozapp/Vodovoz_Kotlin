package com.vodovoz.app.feature.map.test.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class MapTestResponse(
    val DELIVERY_DATE: String?,
    val ID: String?,
    val IS_DELIVERY_FREE: Boolean?,
    val UF_COORDS_0: String?,
    val UF_COORDS_1: String?,
    val UF_COORDS_FROM_0: String?,
    val UF_COORDS_FROM_1: String?,
    val UF_DATE: String?,
    val UF_DELIVERY_PRICE: UFDELIVERYPRICE?,
    val UF_LENGTH: String?,
    val UF_MAP_ID: String?,
    val UF_NAME: String?,
    val UF_NAME_ORDER_FORM: String?,
    val UF_PRICE_FIX: String?,
    val UF_PRICE_KM: String?,
    val UF_ZONE_ID: String?
) : Parcelable