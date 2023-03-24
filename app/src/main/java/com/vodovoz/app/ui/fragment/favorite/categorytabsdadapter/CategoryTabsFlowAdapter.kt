package com.vodovoz.app.ui.fragment.favorite.categorytabsdadapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.CategoryUI.Companion.CATEGORY_UI_VIEW_TYPE

class CategoryTabsFlowAdapter(
    private val clickListener: CategoryTabsFlowClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            CATEGORY_UI_VIEW_TYPE -> {
                CategoryTabsFlowViewHolder(getViewFromInflater(R.layout.view_holder_brand_filter_value, parent), clickListener)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}