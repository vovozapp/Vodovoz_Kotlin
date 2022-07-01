package com.vodovoz.app.ui.components.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridMarginDecoration(
    private val spaceSize: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) % 2 == 0) {
                left = spaceSize
                right = spaceSize/2
            } else {
                left = spaceSize/2
                right = spaceSize
            }
            top = spaceSize
            bottom = spaceSize
        }
    }

}