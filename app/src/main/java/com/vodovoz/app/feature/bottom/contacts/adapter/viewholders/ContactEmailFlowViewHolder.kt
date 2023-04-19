package com.vodovoz.app.feature.bottom.contacts.adapter.viewholders

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderContactEmailBinding
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.ui.model.EmailUI

class ContactEmailFlowViewHolder(
    view: View,
    clickListener: ContactsClickListener
) : ItemViewHolder<EmailUI>(view) {

    private val binding: ViewHolderContactEmailBinding = ViewHolderContactEmailBinding.bind(view)

    init {

    }

    override fun bind(item: EmailUI) {
        super.bind(item)

    }

}