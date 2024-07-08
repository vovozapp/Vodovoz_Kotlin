package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParentSectionDataUI(
    val title: String,
    val sectionDataEntityUIList: List<SectionDataUI>,
    val isSelected: Boolean = false,
): Item, Parcelable {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_home_sections_tabs
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ParentSectionDataUI) return false

        return title == item.title
    }
}
