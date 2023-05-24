package com.vodovoz.app.feature.profile.notificationsettings.adapter

import com.vodovoz.app.feature.profile.notificationsettings.model.NotSettingsItem

interface NotSettingsClickListener {

    fun onItemChecked(item: NotSettingsItem)
}