package com.vodovoz.app.ui.components.adapter.searchWordAdapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SearchMarginDecoration(
    private val spaceSize: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                left = spaceSize
            }
            top = spaceSize/2
            bottom = spaceSize/2
            right = spaceSize
        }
    }

}