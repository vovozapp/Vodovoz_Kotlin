package com.vodovoz.app.feature.profile.cats.adapter.inner

import android.view.View
import com.vodovoz.app.common.content.itemadapter.ItemViewHolder
import com.vodovoz.app.databinding.ItemProfileInsideCategoryUiBinding
import com.vodovoz.app.feature.profile.adapter.ProfileFlowClickListener
import com.vodovoz.app.feature.profile.cats.ProfileInsideCategoryUI

class ProfileCategoriesInnerViewHolder(
    view: View,
    clickListener: ProfileFlowClickListener
) : ItemViewHolder<ProfileInsideCategoryUI>(view) {

    private val binding: ItemProfileInsideCategoryUiBinding = ItemProfileInsideCategoryUiBinding.bind(view)

    override fun bind(item: ProfileInsideCategoryUI) {
        super.bind(item)

        binding.insideCategoryTv.text = item.name
    }

}