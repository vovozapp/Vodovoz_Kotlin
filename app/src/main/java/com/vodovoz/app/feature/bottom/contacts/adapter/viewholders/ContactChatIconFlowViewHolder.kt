package com.vodovoz.app.feature.bottom.contacts.adapter.viewholders

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderContactChatIconBinding
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.ui.model.ChatUI

class ContactChatIconFlowViewHolder(
    view: View,
    clickListener: ContactsClickListener
) : ItemViewHolder<ChatUI>(view) {

    private val binding: ViewHolderContactChatIconBinding = ViewHolderContactChatIconBinding.bind(view)

    init {
        binding.icon.setOnClickListener {
            val item = item ?:return@setOnClickListener
            clickListener.onChatIconSelect(item)
        }
    }

    override fun bind(item: ChatUI) {
        super.bind(item)

        Glide
            .with(itemView.context)
            .load(item.icon)
            .into(binding.icon)
    }

}