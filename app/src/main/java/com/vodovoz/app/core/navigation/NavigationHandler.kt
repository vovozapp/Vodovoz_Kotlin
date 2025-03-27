package com.vodovoz.app.core.navigation

import androidx.navigation.NavController

data object NavigationHandler {

    fun navigate(id: String, navController: NavController) {
        when (id) {
            CHANGE_PASSWORD_ROUTE -> {
                navController.navigateToChangePassword()
            }

            CERTIFICATE_ACTIVATION_ROUTE -> {
                navController.navigateToCertificateActivation()
            }

            else -> {

            }
        }
    }


    private const val CHANGE_PASSWORD_ROUTE = "parol"
    private const val CERTIFICATE_ACTIVATION_ROUTE = "kodslova"

}