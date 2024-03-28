package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.model.common.ActionEntity
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
    var categoryUIList: List<CategoryUI> = listOf(),
    var isSelected: Boolean = false,
    val filterCode: String = "",
    val sortTypeList: SortTypeListUI? = null,
    val actionEntity: ActionEntity? = null,
) : Parcelable, Item {

    companion object {
        const val CATEGORY_UI_VIEW_TYPE = -4400
    }

    override fun getItemViewType(): Int {
        return CATEGORY_UI_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is CategoryUI) return false

        return id == item.id
    }
}