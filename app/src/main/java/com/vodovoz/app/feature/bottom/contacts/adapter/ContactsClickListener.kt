package com.vodovoz.app.feature.bottom.contacts.adapter

import com.vodovoz.app.ui.model.ChatUI
import com.vodovoz.app.ui.model.EmailUI
import com.vodovoz.app.ui.model.PhoneUI

interface ContactsClickListener {

    fun onPhoneSelect(item: PhoneUI)
    fun onChatIconSelect(item: ChatUI)

    fun onEmailSelect(item: EmailUI)

}