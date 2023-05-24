package com.vodovoz.app.feature.profile.notificationsettings.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.itemadapter.Item

class NotSettingsController(clickListener: NotSettingsClickListener) {

    private val notSettingsAdapter = NotSettingsAdapter(clickListener)

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun submitList(list: List<Item>) {
        notSettingsAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = notSettingsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}