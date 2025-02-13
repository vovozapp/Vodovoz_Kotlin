package com.vodovoz.app.feature.bottom.contacts.adapter.viewholders

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderContactPhoneBinding
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.ui.model.PhoneUI

class ContactPhoneFlowViewHolder(
    view: View,
    clickListener: ContactsClickListener
) : ItemViewHolder<PhoneUI>(view) {

    private val binding: ViewHolderContactPhoneBinding = ViewHolderContactPhoneBinding.bind(view)

    init {
        binding.value.setOnClickListener {
            val item = item ?:return@setOnClickListener
            clickListener.onPhoneSelect(item)
        }
    }

    override fun bind(item: PhoneUI) {
        super.bind(item)
        binding.name.text = item.name
        binding.value.text = item.value
    }

}