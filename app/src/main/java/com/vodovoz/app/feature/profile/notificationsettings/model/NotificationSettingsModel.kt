package com.vodovoz.app.feature.profile.notificationsettings.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    val title: String,
    @Json(name = "LISTADATA")
    val settingsList: List<NotSettingsItem>,
    @Json(name = "TELEFON")
    val myPhone: NotSettingsItem,
)

@JsonClass(generateAdapter = true)
data class NotSettingsItem(
    @Json(name = "ACTIVE")
    val active: String,
    @Json(name = "KLYCH")
    val id: String,
    @Json(name = "NAME")
    val name: String,
)