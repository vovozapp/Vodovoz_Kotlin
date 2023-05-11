package com.vodovoz.app.feature.bottom.contacts.adapter.viewholders

import android.view.View
import com.bumptech.glide.Glide
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.data.util.ImagePathParser.parseImagePath
import com.vodovoz.app.databinding.ViewHolderContactChatIconBinding
import com.vodovoz.app.feature.bottom.contacts.adapter.ContactsClickListener
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.cats.DANNYECHAT
import com.vodovoz.app.ui.model.ChatUI

class ContactProfileFlowViewHolder(
    view: View,
    clickListener: ProfileFlowClickListener
) : ItemViewHolder<DANNYECHAT>(view) {

    private val binding: ViewHolderContactChatIconBinding = ViewHolderContactChatIconBinding.bind(view)

    init {
        binding.icon.setOnClickListener {
            val item = item ?:return@setOnClickListener

            when(item.ID) {
                "chat" -> {
                    clickListener.onMyChatClick()
                }
                "viber" -> {
                    clickListener.onViberClick(item.CHATDANIOS)
                }
                "telega" -> {
                    clickListener.onTelegramClick(item.CHATDANIOS)
                }
                "watsup" -> {
                    clickListener.onWhatsUpClick(item.CHATDAN)
                }
            }

        }
    }

    override fun bind(item: DANNYECHAT) {
        super.bind(item)

        Glide
            .with(itemView.context)
            .load(item.NAME?.parseImagePath())
            .into(binding.icon)
    }

}