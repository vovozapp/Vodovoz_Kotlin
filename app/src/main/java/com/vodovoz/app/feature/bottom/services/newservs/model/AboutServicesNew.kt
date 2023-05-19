package com.vodovoz.app.feature.bottom.services.newservs.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AboutServicesNew(
    val status: String?,
    val message: String?,
    @Json(name = "data")
    val aboutServicesData: AboutServicesData?
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class AboutServicesData(
    @Json(name = "NAME")
    val description: String?,
    @Json(name = "OPIS")
    val servicesList: List<ServiceNew>?,
    @Json(name = "TITLE")
    val title: String?
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ServiceNew(
    @Json(name = "ID")
    val id: String?,
    @Json(name = "NAME")
    val name: String?,
    @Json(name = "PREVIEW_PICTURE")
    val preview: String?
): Parcelable