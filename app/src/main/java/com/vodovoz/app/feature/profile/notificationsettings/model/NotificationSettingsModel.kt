package com.vodovoz.app.feature.profile.notificationsettings.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.home.viewholders.homepopulars.HomePopulars

@JsonClass(generateAdapter = true)
data class NotificationSettingsModel(
    @Json(name = "data")
    val notSettingsData: NotificationSettingsData?,
    val message: String?,
    val status: String?,
)

@JsonClass(generateAdapter = true)
data class NotificationSettingsData(
    @Json(name = "INFORMIROVANIE")
    val title: String?,
    @Json(name = "LISTADATA")
    val settingsList: List<NotSettingsItem>?,
    @Json(name = "TELEFON")
    val myPhone: NotSettingsItem?,
)

@JsonClass(generateAdapter = true)
data class NotSettingsItem(
    @Json(name = "ACTIVE")
    val active: String?,
    @Json(name = "KLYCH")
    val id: String?,
    @Json(name = "NAME")
    val name: String?,
) : Item {
    override fun getItemViewType(): Int {
        return R.layout.item_notification_settings
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is NotSettingsItem) return false

        return this.id == item.id
    }

}