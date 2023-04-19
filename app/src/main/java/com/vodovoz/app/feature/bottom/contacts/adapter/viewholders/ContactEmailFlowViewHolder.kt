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
        binding.value.setOnClickListener {
            val item = item ?:return@setOnClickListener
            clickListener.onEmailSelect(item)
        }
    }

    override fun bind(item: EmailUI) {
        super.bind(item)

        binding.name.text = item.name
        binding.value.text = item.value
    }

}