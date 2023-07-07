package com.vodovoz.app.feature.bottom.contacts.adapter.controller

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsFlowAdapter

class PhoneController(
    listener: ContactsClickListener,
) : ItemController(ContactsFlowAdapter(listener)) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)

            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

            ContextCompat.getDrawable(context, R.drawable.bg_border_gray)
                ?.let { divider.setDrawable(it) }

            addItemDecoration(divider)
        }
    }
}