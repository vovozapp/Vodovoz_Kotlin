package com.vodovoz.app.util.extensions

import com.vodovoz.app.BuildConfig
import timber.log.Timber

inline fun debugLog(message: () -> String) {
    if (BuildConfig.DEBUG) {
        Timber.d(message.invoke())
    }
}