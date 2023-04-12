package com.vodovoz.app.feature.profile.cats.adapter

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener

class ProfileCategoriesAdapter(
    private val clickListener: ProfileFlowClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.item_profile_category_ui -> {
                ProfileCategoriesViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }

            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}