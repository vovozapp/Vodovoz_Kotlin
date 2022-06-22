package com.vodovoz.app.ui.components.adapter.primaryProductsFiltersAdapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ProductsFiltersMarginDecoration(
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
            bottom = spaceSize/2
            top = spaceSize/2
            right = spaceSize
        }
    }

}