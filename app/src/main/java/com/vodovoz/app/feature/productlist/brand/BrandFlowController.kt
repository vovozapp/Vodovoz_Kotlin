package com.vodovoz.app.feature.productlist.brand

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.ui.decoration.ProductsFiltersMarginDecoration

class BrandFlowController(
    private val clickListener: BrandFlowClickListener
) {

    private val brandAdapter = BrandFlowAdapter(clickListener)

    fun bind(recyclerView: RecyclerView, space: Int) {
        initList(recyclerView, space)
    }

    fun submitList(list: List<Item>) {
        brandAdapter.submitList(list)
    }

    private fun initList(recyclerView: RecyclerView, space: Int) {
        with(recyclerView) {
            adapter = brandAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(ProductsFiltersMarginDecoration(space/2))
        }
    }
}