package com.vodovoz.app.ui.extensions

import android.view.View
import android.view.ViewGroup
import com.vodovoz.app.ui.view.CustomSnackbar

object SnackbarExtensions {

    fun View.showSnackError(title: String, description: String) {
        CustomSnackbar.make(this as ViewGroup).show()
    }
}