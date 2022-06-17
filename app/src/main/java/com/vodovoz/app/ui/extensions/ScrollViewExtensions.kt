package com.vodovoz.app.ui.extensions

import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout

object ScrollViewExtensions {

    fun NestedScrollView.setScrollElevation(appBarLayout: AppBarLayout) {
        viewTreeObserver.addOnScrollChangedListener {
            appBarLayout.translationZ =
                if (canScrollVertically(-1)) 16f
                else 0f
        }
    }

}