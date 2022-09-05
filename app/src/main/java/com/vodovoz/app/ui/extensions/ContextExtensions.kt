package com.vodovoz.app.ui.extensions

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION
import com.vodovoz.app.BuildConfig

object ContextExtensions {

    fun Context.isTablet(): Boolean {
        val xlarge = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_XLARGE
        val large = this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }

    fun Context.getDeviceInfo(): String {
        val builder = StringBuilder()
        when(isTablet()) {
            true -> builder.append("Планшет Android:")
            false -> builder.append("Телефон Android:")
        }
        builder
            .append(" ").append(VERSION.RELEASE)
            .append(" ").append("Версия:").append(" ").append(BuildConfig.VERSION_NAME)
        return builder.toString()
    }

    fun openURL(url: String) {

    }

}