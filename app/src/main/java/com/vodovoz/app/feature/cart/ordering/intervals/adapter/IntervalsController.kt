package com.vodovoz.app.feature.cart.ordering.intervals.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class IntervalsController(
    clickListener: ((Item) -> Unit),
) : ItemController(IntervalsAdapter(clickListener)) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addMarginDecoration { rect, view, parent, state ->
                val space8 = context.resources.getDimension(R.dimen.space_8).toInt()
                if (parent.getChildAdapterPosition(view) == 0) rect.top = space8
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom =
                    space8
            }
        }
    }
}