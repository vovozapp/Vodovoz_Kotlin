package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.*
import com.vodovoz.app.mapper.ChatBundleMapper.mapToUI
import com.vodovoz.app.mapper.ChatMapper.mapToUI
import com.vodovoz.app.mapper.EmailMapper.mapToUI
import com.vodovoz.app.mapper.PhoneMapper.mapToUI
import com.vodovoz.app.ui.model.*
import java.util.*

object ContactsBundleMapper {

    fun ContactsBundleEntity.mapToUI() = ContactsBundleUI(
        title = title,
        phoneUIList = phoneEntityList.mapToUI(),
        emailUIList = emailEntityList.mapToUI(),
        chatsBundleUI = chatsBundleEntity?.mapToUI()
    )

}

object ChatBundleMapper {

    fun ChatsBundleEntity.mapToUI() = ChatsBundleUI(
        name = name,
        chatUIList = chatEntityList.mapToUI()
    )

}

object EmailMapper {

    fun List<EmailEntity>.mapToUI(): List<EmailUI> = mutableListOf<EmailUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun EmailEntity.mapToUI() = EmailUI(
        name = name,
        value = value,
        type = type
    )

}

object PhoneMapper {

    fun List<PhoneEntity>.mapToUI(): List<PhoneUI> = mutableListOf<PhoneUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun PhoneEntity.mapToUI() = PhoneUI(
        name = name,
        value = value,
        type = type
    )
}

object ChatMapper {

    fun List<ChatEntity>.mapToUI(): List<ChatUI> = mutableListOf<ChatUI>().also { uiList ->
        forEach { uiList.add(it.mapToUI()) }
    }

    fun ChatEntity.mapToUI() = ChatUI(
        icon = icon,
        action = action,
        type = type
    )

}