package com.vodovoz.app.ui.extensions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
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

    fun RecyclerView.addMarginDecoration(
        callback: (
            rect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) -> Unit
    ) {
        this.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    callback(outRect, view, parent, state)
                }
            }
        )
    }

    fun ViewPager2.addMarginDecoration(
        callback: (
            rect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) -> Unit
    ) {
        this.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    callback(outRect, view, parent, state)
                }
            }
        )
    }

}