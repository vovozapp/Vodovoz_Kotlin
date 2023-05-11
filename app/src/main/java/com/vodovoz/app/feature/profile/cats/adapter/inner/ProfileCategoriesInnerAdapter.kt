package com.vodovoz.app.feature.profile.cats.adapter.inner

import android.view.ViewGroup
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.ItemAdapter
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.feature.bottom.contacts.adapter.viewholders.ContactProfileFlowViewHolder
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.cats.DANNYECHAT

class ProfileCategoriesInnerAdapter(
    private val clickListener: ProfileFlowClickListener
) : ItemAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder<out Item> {
        return when (viewType) {
            R.layout.item_profile_inside_category_ui -> {
                ProfileCategoriesInnerViewHolder(getViewFromInflater(viewType, parent), clickListener)
            }
            DANNYECHAT.DANNAYE_CHAT_VIEW_TYPE -> {
                ContactProfileFlowViewHolder(getViewFromInflater(R.layout.view_holder_contact_chat_icon, parent), clickListener)
            }

            else -> {
                throw IllegalArgumentException("Adapter item viewType not found")
            }
        }
    }
}