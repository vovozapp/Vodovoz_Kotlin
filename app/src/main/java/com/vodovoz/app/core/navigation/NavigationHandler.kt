package com.vodovoz.app.core.navigation

import androidx.navigation.NavController

data object NavigationHandler {

    fun navigate(id: String, navController: NavController) {
        when (id) {
            CHANGE_PASSWORD_ROUTE -> {
                navController.navigateToChangePassword()
            }

            else -> {

            }
        }
    }


    private const val CHANGE_PASSWORD_ROUTE = "parol"

}