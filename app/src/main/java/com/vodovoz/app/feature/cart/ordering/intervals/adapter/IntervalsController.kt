package com.vodovoz.app.feature.cart.ordering.intervals.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.R
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.extensions.RecyclerViewExtensions.addMarginDecoration

class IntervalsController(
    clickListener: IntervalsClickListener,
    context: Context
) : ItemController(IntervalsAdapter(clickListener)){

    private val space8 by lazy { context.resources.getDimension(R.dimen.space_8).toInt() }

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            addMarginDecoration { rect, view, parent, state ->
                if (parent.getChildAdapterPosition(view) == 0) rect.top = space8
                if (parent.getChildAdapterPosition(view) == state.itemCount - 1) rect.bottom = space8
            }
        }
    }
}