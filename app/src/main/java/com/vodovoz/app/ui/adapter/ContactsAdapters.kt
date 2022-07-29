package com.vodovoz.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.data.model.common.EmailEntity
import com.vodovoz.app.databinding.ViewHolderContactChatIconBinding
import com.vodovoz.app.databinding.ViewHolderContactChatsBundleBinding
import com.vodovoz.app.databinding.ViewHolderContactEmailBinding
import com.vodovoz.app.databinding.ViewHolderContactPhoneBinding
import com.vodovoz.app.ui.model.ChatUI
import com.vodovoz.app.ui.model.ChatsBundleUI
import com.vodovoz.app.ui.model.EmailUI
import com.vodovoz.app.ui.model.PhoneUI
import com.vodovoz.app.ui.view_holder.ChatIconViewHolder
import com.vodovoz.app.ui.view_holder.ChatsBundleViewHolder
import com.vodovoz.app.ui.view_holder.EmailViewHolder
import com.vodovoz.app.ui.view_holder.PhoneViewHolder

class PhoneContactsAdapter(
    private val onPhoneSelect: (PhoneUI) -> Unit,
    private val onChatIconSelect: (ChatUI) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val PHONE_VIEW_TYPE = 0
        private const val CHATS_VIEW_TYPE = 1
    }

    var phoneUIList = listOf<PhoneUI>()
    var chatsBundleUI: ChatsBundleUI? = null

    override fun getItemViewType(position: Int) = when(position == phoneUIList.size) {
        false -> PHONE_VIEW_TYPE
        true -> CHATS_VIEW_TYPE
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when(viewType) {
        PHONE_VIEW_TYPE -> PhoneViewHolder(
            binding = ViewHolderContactPhoneBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onPhoneSelect = onPhoneSelect
        )
        CHATS_VIEW_TYPE -> ChatsBundleViewHolder(
            binding = ViewHolderContactChatsBundleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            context = parent.context,
            onChatIconSelect = onChatIconSelect
        )
        else -> throw Exception("Unknown type")
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) = when(getItemViewType(position)) {
        PHONE_VIEW_TYPE -> (holder as PhoneViewHolder).onBind(phoneUIList[position])
        CHATS_VIEW_TYPE-> (holder as ChatsBundleViewHolder).onBind(chatsBundleUI!!)
        else -> throw Exception("Unknown type")
    }

    override fun getItemCount() = when(chatsBundleUI) {
        null -> phoneUIList.size
        else -> phoneUIList.size + 1
    }

}

class EmailContactsAdapter(
    private val onEmailSelect: (EmailUI) -> Unit
) : RecyclerView.Adapter<EmailViewHolder>() {

    var emailUIList = listOf<EmailUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = EmailViewHolder(
        binding = ViewHolderContactEmailBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onEmailSelect = onEmailSelect
    )

    override fun onBindViewHolder(
        holder: EmailViewHolder,
        position: Int
    ) = holder.onBind(emailUIList[position])

    override fun getItemCount() = emailUIList.size

}

class ChatIconsAdapter(
    private val onChatIconSelect: (ChatUI) -> Unit
) : RecyclerView.Adapter<ChatIconViewHolder>() {

    var chatUIList = listOf<ChatUI>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ChatIconViewHolder(
        binding = ViewHolderContactChatIconBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        context = parent.context,
        onChatIconSelect = onChatIconSelect
    )

    override fun onBindViewHolder(
        holder: ChatIconViewHolder,
        position: Int
    ) = holder.onBind(chatUIList[position])

    override fun getItemCount() = chatUIList.size

}