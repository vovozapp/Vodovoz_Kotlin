package com.vodovoz.app.ui.decoration

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
            left = if (parent.getChildAdapterPosition(view) == 0) {
                space
            } else {
                space / 4
            }
            right = if (parent.getChildAdapterPosition(view) == itemCount - 1) {
                space
            } else {
                space / 4
            }
            top = space/4
            bottom = space/4
        }
    }

}