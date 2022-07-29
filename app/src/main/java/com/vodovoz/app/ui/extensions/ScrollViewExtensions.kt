package com.vodovoz.app.ui.extensions

import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout

object ScrollViewExtensions {

    fun NestedScrollView.setScrollElevation(view: View) {
        viewTreeObserver.addOnScrollChangedListener {
            view.translationZ =
                if (canScrollVertically(-1)) 16f
                else 0f
        }
    }

    fun ScrollView.setScrollElevation(appBarLayout: AppBarLayout) {
        viewTreeObserver.addOnScrollChangedListener {
            appBarLayout.translationZ =
                if (canScrollVertically(-1)) 16f
                else 0f
        }
    }

}