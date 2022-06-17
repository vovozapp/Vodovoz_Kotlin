package com.vodovoz.app.ui.components.adapter.orderSliderAdapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OrderSliderMarginDecoration(
    private val spaceSize: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            left = spaceSize
            top = spaceSize / 2
            right = spaceSize
            bottom = spaceSize / 2
        }
    }

}