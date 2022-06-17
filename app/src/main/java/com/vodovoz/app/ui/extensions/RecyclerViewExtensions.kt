package com.vodovoz.app.ui.extensions

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

object RecyclerViewExtensions {

    fun RecyclerView.setScrollElevation(appBarLayout: AppBarLayout) {
        addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    appBarLayout.elevation =
                        if (canScrollVertically(-1)) 16f
                        else 0f
                }
            }
        )
    }

}