package com.vodovoz.app.ui.components.adapter.bannerSliderAdapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BannerSliderMarginDecoration(
    private val marginTop: Int,
    private val marginBottom: Int,
    private val marginLeft: Int,
    private val marginRight: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = marginTop
            bottom = marginBottom
            left = marginLeft
            right = marginRight
        }
    }

}