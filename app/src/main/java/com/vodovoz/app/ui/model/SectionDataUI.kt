package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class SectionDataUI(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val promotionId: Int,
    val cookieLink: String,
) : Item, Parcelable {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_section_top
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is SectionDataUI) return false

        return id == item.id
    }
}
