package com.vodovoz.app.feature.profile.core

import android.content.Context
import androidx.navigation.NavController
import com.vodovoz.app.R
import com.vodovoz.app.core.navigation.navigateToWebView
import com.vodovoz.app.util.extensions.dialPhoneNumber
import com.vodovoz.app.util.extensions.startTelegram
import com.vodovoz.app.util.extensions.startViber
import com.vodovoz.app.util.extensions.startWhatsUp

object ProfileChatsNavigator {

    fun navigate(chatId: String, data: String, navController: NavController, context: Context) {
        when (chatId) {
            "chat" -> {
                navController.navigateToWebView(
                    "http://jivo.chat/mk31km1IlP", ""
                )
            }

            "viber" -> {
                context.startViber(data)
            }

            "telega" -> {
                context.startTelegram(data)
            }

            "watsup" -> {
                context.startWhatsUp(data)
            }

            "telefon" -> {
                context.dialPhoneNumber(data)
            }

            else -> {
                //todo - navigate to writ message
                //navController.navi()
            }
        }
    }

}