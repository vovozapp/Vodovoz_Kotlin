package com.vodovoz.app.feature.bottom.contacts.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.bottom.contacts.adapter.viewholders.ContactChatBundleFlowViewHolder
import com.vodovoz.app.feature.bottom.contacts.adapter.viewholders.ContactChatIconFlowViewHolder
import com.vodovoz.app.feature.bottom.contacts.adapter.viewholders.ContactEmailFlowViewHolder
import com.vodovoz.app.feature.bottom.contacts.adapter.viewholders.ContactPhoneFlowViewHolder

class ContactsFlowAdapter(
    private val clickListener: ContactsClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.view_holder_contact_chats_bundle -> {
                ContactChatBundleFlowViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_contact_email -> {
                ContactEmailFlowViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_contact_chat_icon -> {
                ContactChatIconFlowViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            R.layout.view_holder_contact_phone -> {
                ContactPhoneFlowViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}