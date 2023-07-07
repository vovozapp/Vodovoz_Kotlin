package com.vodovoz.app.feature.productlist.brand

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.common.content.ItemController
import com.vodovoz.app.ui.decoration.ProductsFiltersMarginDecoration

class BrandFlowController(
    clickListener: BrandFlowClickListener,
    private val space: Int,
) : ItemController(BrandFlowAdapter(clickListener)) {

    override fun initList(recyclerView: RecyclerView) {
        super.initList(recyclerView)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(ProductsFiltersMarginDecoration(space / 2))
        }
    }
}