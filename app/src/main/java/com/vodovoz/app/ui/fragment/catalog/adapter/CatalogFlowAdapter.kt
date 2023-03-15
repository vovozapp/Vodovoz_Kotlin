package com.vodovoz.app.ui.fragment.catalog.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.CategoryUI.Companion.CATEGORY_UI_VIEW_TYPE

class CatalogFlowAdapter(
    private val nestingPosition: Int,
    private val clickListener: CatalogFlowClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when(viewType) {
            CATEGORY_UI_VIEW_TYPE-> {
                CatalogFlowViewHolder(getViewFromInflater(R.layout.view_holder_catalog_category , parent), clickListener, nestingPosition)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}