package com.vodovoz.app.feature.bottom.contacts.adapter.controller

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsFlowAdapter

class EmailController(
    listener: ContactsClickListener,
) {

    private val emailAdapter = ContactsFlowAdapter(listener)

    fun bind(recyclerView: RecyclerView) {
        initList(recyclerView)
    }

    fun submitList(list: List<Item>) {
        emailAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView) {
        with(recyclerView) {
            adapter = emailAdapter
            layoutManager = LinearLayoutManager(context)
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            ContextCompat.getDrawable(context, R.drawable.bg_border_gray)
                ?.let { divider.setDrawable(it) }
            addItemDecoration(divider)
        }
    }
}