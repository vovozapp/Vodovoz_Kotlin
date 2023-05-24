package com.vodovoz.app.feature.profile.notificationsettings.adapter

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemNotificationSettingsBinding
import com.vodovoz.app.feature.profile.notificationsettings.model.NotSettingsItem

class NotSettingsViewHolder(
    view: View,
    clickListener: NotSettingsClickListener,
) : ItemViewHolder<NotSettingsItem>(view) {

    private val binding: ItemNotificationSettingsBinding = ItemNotificationSettingsBinding.bind(view)

    init {
        binding.settingsSwitch.setOnCheckedChangeListener { p0, p1 ->
            val item = item ?: return@setOnCheckedChangeListener
            clickListener.onItemChecked(item.copy(
                active = if (p1) {
                    "Y"
                } else {
                    "N"
                }
            ))
        }
    }

    override fun bind(item: NotSettingsItem) {
        super.bind(item)

        binding.settingsSwitch.isChecked = item.active == "Y"

        binding.settingsTv.text = item.name

    }
}