package com.vodovoz.app.feature.bottom.contacts.adapter.viewholders

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderContactChatsBundleBinding
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.ui.model.ChatsBundleUI

class ContactChatBundleFlowViewHolder(
    view: View,
    clickListener: ContactsClickListener
) : ItemViewHolder<ChatsBundleUI>(view) {

    private val binding: ViewHolderContactChatsBundleBinding = ViewHolderContactChatsBundleBinding.bind(view)

    init {

    }

    override fun bind(item: ChatsBundleUI) {
        super.bind(item)

    }

}