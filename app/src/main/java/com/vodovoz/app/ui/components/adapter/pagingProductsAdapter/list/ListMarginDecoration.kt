package com.vodovoz.app.ui.components.adapter.pagingProductsAdapter.list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ListMarginDecoration(
    private val spaceSize: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = spaceSize
            bottom = spaceSize
            right = spaceSize
        }
    }

}