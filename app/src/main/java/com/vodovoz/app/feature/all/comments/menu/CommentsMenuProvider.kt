package com.vodovoz.app.feature.all.comments.menu

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import com.vodovoz.app.R

class CommentsMenuProvider(
    private val listener: () -> Unit
) : MenuProvider {


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.all_comments_by_product_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sendComment -> {
                listener()
            }
        }
        return false
    }
}