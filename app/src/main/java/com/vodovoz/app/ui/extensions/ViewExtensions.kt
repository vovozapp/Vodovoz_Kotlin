package com.vodovoz.app.ui.extensions

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.view.View
import android.view.ViewTreeObserver
import com.google.android.material.snackbar.Snackbar

object ViewExtensions {

    fun View.onRenderFinished(callback: (width: Int, height: Int) -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    callback(width, height)
                }
            }
        )
    }

    fun View.openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(this.context.packageManager) != null) {
            this.context.startActivity(intent)
        } else {
            Snackbar.make(this, "Нет подходящего приложения, чтобы выполнить это действие", Snackbar.LENGTH_LONG).show()
        }
    }

}