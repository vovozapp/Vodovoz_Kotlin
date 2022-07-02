package com.vodovoz.app.ui.extensions

import android.view.View
import android.view.ViewTreeObserver

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

}