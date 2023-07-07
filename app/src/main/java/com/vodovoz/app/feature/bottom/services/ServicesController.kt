package com.vodovoz.app.feature.bottom.services

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.feature.bottom.services.adapter.ServicesAdapter
import com.vodovoz.app.feature.bottom.services.adapter.ServicesClickListener

class ServicesController(
    clickListener: ServicesClickListener,
) : ItemController(ServicesAdapter(clickListener)) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
        }
    }
}