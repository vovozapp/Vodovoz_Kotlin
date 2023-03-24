package com.vodovoz.app.ui.fragment.favorite.categorytabsdadapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.decoration.CategoryTabsMarginDecoration

class CategoryTabsFlowController(
    private val clickListener: CategoryTabsFlowClickListener
) {

    private val tabsAdapter = CategoryTabsFlowAdapter(clickListener)

    fun bind(recyclerView: RecyclerView, space: Int) {
        initList(recyclerView, space)
    }

    fun submitList(list: List<Item>) {
        tabsAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView, space: Int) {
        with(recyclerView) {
            adapter = tabsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(CategoryTabsMarginDecoration(space/2))
        }
    }
}