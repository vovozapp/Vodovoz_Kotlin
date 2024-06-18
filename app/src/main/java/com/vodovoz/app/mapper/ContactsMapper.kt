package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.ChatEntity
import com.vodovoz.app.data.model.common.ChatsBundleEntity
import com.vodovoz.app.data.model.common.ContactsBundleEntity
import com.vodovoz.app.data.model.common.EmailEntity
import com.vodovoz.app.data.model.common.PhoneEntity
import com.vodovoz.app.mapper.ChatBundleMapper.mapToUI
import com.vodovoz.app.mapper.ChatMapper.mapToUI
import com.vodovoz.app.mapper.EmailMapper.mapToUI
import com.vodovoz.app.mapper.PhoneMapper.mapToUI
import com.vodovoz.app.ui.model.ChatUI
import com.vodovoz.app.ui.model.ChatsBundleUI
import com.vodovoz.app.ui.model.ContactsBundleUI
import com.vodovoz.app.ui.model.EmailUI
import com.vodovoz.app.ui.model.PhoneUI

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