package com.vodovoz.app.ui.fragment.cart.menu

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import com.vodovoz.app.R

class CartMenuProvider(
    private val listener: CartMenuListener
) : MenuProvider {

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.fragment_cart_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.clearCart -> {
                listener.onClearCartClick()
            }
            R.id.orderHistory -> {
                listener.onHistoryClick()
            }
        }
        return true
    }
}