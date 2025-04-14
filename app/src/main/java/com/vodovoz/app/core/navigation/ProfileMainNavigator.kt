package com.vodovoz.app.core.navigation

import android.content.Context
import androidx.navigation.NavController
import com.vodovoz.app.R
import com.vodovoz.app.core.network.VodovozWebConfig

data object ProfileMainNavigator {

    fun navigate(id: String, navController: NavController, context: Context) {
        when (id) {
            CHANGE_PASSWORD_ROUTE -> {
                navController.navigateToChangePassword()
            }

            CERTIFICATE_ACTIVATION_ROUTE -> {
                navController.navigateToCertificateActivation()
            }

            ORDER_HISTORY_ROUTE -> {
                navController.navigateToOrdersHistory()
            }

            PRODUCTS_HISTORY_ROUTE -> {
                navController.navigateToPastPurchases()
            }

            ADDRESSES_ROUTE -> {
                navController.navigateToAddresses()
            }

            QUESTIONNAIRES_ROUTE -> {
                navController.navigateToQuestionnaires()
            }

            ABOUT_DELIVERY_ROUTE -> {
                navController.navigateToWebView(
                    VodovozWebConfig.ABOUT_DELIVERY_URL,
                    context.getString(R.string.about_delivery)
                )

            }

            ABOUT_PAYMENT_ROUTE -> {
                navController.navigateToWebView(
                    VodovozWebConfig.ABOUT_PAYMENT_URL,
                    context.getString(R.string.about_pay)
                )
            }

            SETTINGS_NOTIFICATIONS_ROUTE -> {
                navController.navigateToNotificationSettings()
            }

            ABOUT_APP_ROUTE -> {
                navController.navigateToAboutApp()
            }

            else -> {

            }
        }
    }


    private const val CHANGE_PASSWORD_ROUTE = "parol"
    private const val CERTIFICATE_ACTIVATION_ROUTE = "kodslova"
    private const val ORDER_HISTORY_ROUTE = "historyzakaz"
    private const val PRODUCTS_HISTORY_ROUTE = "historytovar"
    private const val ADDRESSES_ROUTE = "adressa"
    private const val QUESTIONNAIRES_ROUTE = "anketa"
    private const val ABOUT_DELIVERY_ROUTE = "dostavka"
    private const val ABOUT_PAYMENT_ROUTE = "oplata"
    private const val SETTINGS_NOTIFICATIONS_ROUTE = "yvedomlenie"
    private const val ABOUT_APP_ROUTE = "oprile"


}