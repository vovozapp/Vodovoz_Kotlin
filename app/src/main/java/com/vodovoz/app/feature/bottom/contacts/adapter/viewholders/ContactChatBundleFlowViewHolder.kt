package com.vodovoz.app.feature.bottom.contacts.adapter.viewholders

import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ViewHolderContactChatsBundleBinding
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsFlowAdapter
import com.vodovoz.app.ui.model.ChatsBundleUI

class ContactChatBundleFlowViewHolder(
    view: View,
    clickListener: ContactsClickListener
) : ItemViewHolder<ChatsBundleUI>(view) {

    private val binding: ViewHolderContactChatsBundleBinding =
        ViewHolderContactChatsBundleBinding.bind(view)

    private val contactsAdapter = ContactsFlowAdapter(clickListener)

    init {
        binding.chatIconsRecycler.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        binding.chatIconsRecycler.adapter = contactsAdapter
    }

    override fun getState(): Parcelable? {
        return binding.chatIconsRecycler.layoutManager?.onSaveInstanceState()
    }

    override fun setState(state: Parcelable) {
        binding.chatIconsRecycler.layoutManager?.onRestoreInstanceState(state)
    }


    override fun bind(item: ChatsBundleUI) {
        super.bind(item)

        binding.name.text = item.name
        contactsAdapter.submitList(item.chatUIList)
    }

}