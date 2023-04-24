package com.vodovoz.app.feature.productlist.singleroot.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.ui.model.CategoryUI.Companion.CATEGORY_UI_VIEW_TYPE

class SingleRootFlowAdapter(
    private val clickListener: SingleRootClickListener,
    private val nestingPosition: Int
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            CATEGORY_UI_VIEW_TYPE -> {
                SingleRootViewHolder(getViewFromInflater(R.layout.view_holder_single_root_catalog_category, parent), clickListener, nestingPosition)
            }
            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }

}