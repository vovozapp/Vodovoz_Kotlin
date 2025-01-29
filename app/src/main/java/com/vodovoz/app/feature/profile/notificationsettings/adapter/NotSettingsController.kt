package com.vodovoz.app.feature.profile.notificationsettings.adapter

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.ItemController

class NotSettingsController(clickListener: NotSettingsClickListener) :
    ItemController(NotSettingsAdapter(clickListener)) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
        }
    }
}