package com.vodovoz.app.feature.bottom.services

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.bottom.services.adapter.ServicesAdapter
import com.vodovoz.app.feature.bottom.services.adapter.ServicesClickListener

class ServicesController(
    clickListener: ServicesClickListener,
    context: Context
) {

    private val space by lazy {
        context.resources.getDimension(R.dimen.space_16).toInt()
    }

    private val servicesAdapter = ServicesAdapter(clickListener)

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun submitList(list: List<Item>) {
        servicesAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = servicesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
}