package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryUI(
    val id: Long? = null,
    val name: String,
    val shareUrl: String = "",
    val productAmount: String? = null,
    val detailPicture: String? = null,
    var isOpen: Boolean = false,
    val primaryFilterName: String? = null,
    var primaryFilterValueList: List<FilterValueUI> = listOf(),
    var categoryUIList: List<CategoryUI> = listOf()
) : Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_popular_category
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CategoryUI) return false

        return id == item.id
    }
}