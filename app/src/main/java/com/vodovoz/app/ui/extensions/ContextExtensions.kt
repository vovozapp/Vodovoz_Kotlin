package com.vodovoz.app.ui.extensions

import android.content.Context
import android.content.res.Configuration

object ContextExtensions {

    fun Context.isTablet(): Boolean {
        val xlarge = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_XLARGE
        val large = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

}