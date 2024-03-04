package com.vodovoz.app.feature.profile.cats

import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item

data class ProfileCategoryUI(
    val title: String?,
    val insideCategories: List<ProfileInsideCategoryUI>?
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_profile_category_ui
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileCategoryUI) return false

        return this == item
    }

}

data class ProfileInsideCategoryUI(
    val id: String?,
    val name: String?,
    val url: String?,
    val amount: String?,
    val chatUiList: List<DANNYECHAT>?
) : Item {

    override fun getItemViewType(): Int {
        return R.layout.item_profile_inside_category_ui
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ProfileInsideCategoryUI) return false

        return this == item
    }

}