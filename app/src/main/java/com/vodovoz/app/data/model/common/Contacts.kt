package com.vodovoz.app.data.model.common

const val PHONE_TYPE = "telefon"
const val WHATSAPP_TYPE = "watsup"
const val EMAIL_TYPE = "pochta"
const val VIBER_TYPE = "viber"
const val TELEGRAM_TYPE = "telega"
const val CHAT_TYPE = "chat"

data class ContactsBundleEntity(
    val title: String = "",
    val phoneEntityList: List<PhoneEntity> = listOf(),
    val emailEntityList: List<EmailEntity> = listOf(),
    val chatsBundleEntity: ChatsBundleEntity? = null
)

data class ChatsBundleEntity(
    val name: String = "",
    val chatEntityList: List<ChatEntity> = listOf()
)

data class ChatEntity(
    val icon: String = "",
    val action: String = "",
    val type: String = ""
)

data class PhoneEntity(
    val name: String = "",
    val value: String = "",
    val type: String = ""
)

data class EmailEntity(
    val name: String = "",
    val value: String = "",
    val type: String = ""
)