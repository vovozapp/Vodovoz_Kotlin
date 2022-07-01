package com.vodovoz.app.ui.components.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ProductSliderMarginDecoration(
    private val space: Int,
    private val itemCount: Int,
    private val isLast: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            left = space
            if (parent.getChildAdapterPosition(view) == itemCount - 1) {
                right = space
            }
            top = space/2
            bottom = space/2
        }
    }

}