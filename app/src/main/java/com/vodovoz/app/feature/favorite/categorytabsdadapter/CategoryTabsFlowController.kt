package com.vodovoz.app.feature.favorite.categorytabsdadapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.ui.decoration.CategoryTabsMarginDecoration

class CategoryTabsFlowController(
    private val clickListener: CategoryTabsFlowClickListener,
    private val space: Int,
) : ItemController(CategoryTabsFlowAdapter(clickListener)) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(CategoryTabsMarginDecoration(space / 2))
        }
    }
}