package com.vodovoz.app.feature.bottom.services.newservs.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
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
): Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_service_detail_new
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ServiceNew) return false

        return this == item
    }
}