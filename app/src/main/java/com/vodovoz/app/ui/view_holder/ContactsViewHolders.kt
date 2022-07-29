package com.vodovoz.app.ui.view_holder

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vodovoz.app.databinding.ViewHolderContactChatIconBinding
import com.vodovoz.app.databinding.ViewHolderContactChatsBundleBinding
import com.vodovoz.app.databinding.ViewHolderContactEmailBinding
import com.vodovoz.app.databinding.ViewHolderContactPhoneBinding
import com.vodovoz.app.ui.adapter.ChatIconsAdapter
import com.vodovoz.app.ui.model.ChatUI
import com.vodovoz.app.ui.model.ChatsBundleUI
import com.vodovoz.app.ui.model.EmailUI
import com.vodovoz.app.ui.model.PhoneUI

class EmailViewHolder(
    private val binding: ViewHolderContactEmailBinding,
    private val onEmailSelect: (EmailUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.value.setOnClickListener { onEmailSelect(emailUI) } }

    private lateinit var emailUI: EmailUI

    fun onBind(emailUI: EmailUI) {
        this.emailUI = emailUI

        binding.name.text = emailUI.name
        binding.value.text = emailUI.value
    }

}

class PhoneViewHolder(
    private val binding: ViewHolderContactPhoneBinding,
    private val onPhoneSelect: (PhoneUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.value.setOnClickListener { onPhoneSelect(phoneUI) } }

    private lateinit var phoneUI: PhoneUI

    fun onBind(phoneUI: PhoneUI) {
        this.phoneUI = phoneUI

        binding.name.text = phoneUI.name
        binding.value.text = phoneUI.value
    }

}

class ChatsBundleViewHolder(
    private val binding: ViewHolderContactChatsBundleBinding,
    private val context: Context,
    private val onChatIconSelect: (ChatUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private val chatIconsAdapter = ChatIconsAdapter(onChatIconSelect)

    init {
        binding.chatIconsRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.chatIconsRecycler.adapter = chatIconsAdapter
    }

    private lateinit var chatsBundleUI: ChatsBundleUI

    @SuppressLint("NotifyDataSetChanged")
    fun onBind(chatsBundleUI: ChatsBundleUI) {
        this.chatsBundleUI = chatsBundleUI
        binding.name.text = chatsBundleUI.name
        chatIconsAdapter.chatUIList = chatsBundleUI.chatUIList
        chatIconsAdapter.notifyDataSetChanged()
    }

}

class ChatIconViewHolder(
    private val binding: ViewHolderContactChatIconBinding,
    private val context: Context,
    private val onChatIconSelect: (ChatUI) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init { binding.icon.setOnClickListener { onChatIconSelect(chatUI) } }

    private lateinit var chatUI: ChatUI

    fun onBind(chatUI: ChatUI) {
        this.chatUI = chatUI
        Glide.with(context).load(chatUI.icon).into(binding.icon)
    }
}