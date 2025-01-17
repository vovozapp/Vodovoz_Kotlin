package com.vodovoz.app.ui.model

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class CategoryDetailUI(
    val id: Long? = null,
    val name: String,
    val productAmount: Int,
    val productUIList: List<ProductUI>,
    val isSelected: Boolean = false,
    val position: Int? = null
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_product_category
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CategoryDetailUI) return false

        return id == item.id
    }
}