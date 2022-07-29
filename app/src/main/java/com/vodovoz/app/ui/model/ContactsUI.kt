package com.vodovoz.app.ui.model

data class ContactsBundleUI(
    val title: String,
    val phoneUIList: List<PhoneUI>,
    val emailUIList: List<EmailUI>,
    val chatsBundleUI: ChatsBundleUI?
)

data class ChatsBundleUI(
    val name: String,
    val chatUIList: List<ChatUI>
)

data class ChatUI(
    val icon: String,
    val action: String,
    val type: String
)

data class PhoneUI(
    val name: String,
    val value: String,
    val type: String
)

data class EmailUI(
    val name: String,
    val value: String,
    val type: String
)