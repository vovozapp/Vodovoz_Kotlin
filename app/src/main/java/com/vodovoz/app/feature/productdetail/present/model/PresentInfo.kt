package com.vodovoz.app.feature.productdetail.present.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class PresentInfo(
    val status: String?,
    val message: String?,
    @Json(name = "data")
    val presentInfoData: PresentInfoData?,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class PresentInfoData(
    @Json(name = "POLOSKA")
    val bottomLine: BottomLine?,
    @Json(name = "TEXT")
    val text: String?,
    @Json(name = "PROGRESBAR")
    val progress: Int?,
    @Json(name = "LIMITPROGRESBAR")
    val limit: Int?,
    @Json(name = "BACKGROUNDPROGRESBAR")
    val progressBackground: String?,
    @Json(name = "PEREXOD")
    val moveTo: String?,
) : Parcelable {
    val showProgressText: Boolean
        get() = if(progress!= null && limit!= null) progress >= limit else false
}

@Parcelize
@JsonClass(generateAdapter = true)
data class BottomLine(
    @Json(name = "BACKROUND")
    val background: Background?,
    @Json(name = "TEXTCOLOR")
    val textColor: Background?,
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Background(
    @Json(name = "BACKGROUNDPOLOSKA")
    val color: String?,
) : Parcelable