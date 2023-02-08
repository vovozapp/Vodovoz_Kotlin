package com.vodovoz.app.util.extensions

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

inline fun Fragment.addOnBackPressedCallback(crossinline callback: () -> Unit) {
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback.invoke()
        }
    }
    this.requireActivity().onBackPressedDispatcher.addCallback(
        this.viewLifecycleOwner,
        onBackPressedCallback
    )
}

fun Context.sp(value: Float) = (value * resources.displayMetrics.scaledDensity)