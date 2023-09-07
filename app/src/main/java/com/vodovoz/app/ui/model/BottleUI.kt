package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class BottleUI(
    val id: Long = 0,
    val name: String = ""
) : Parcelable, Item {

    companion object{
        const val BOTTLE_VIEW_TYPE = -1000
    }
    override fun getItemViewType(): Int {
        return BOTTLE_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is BottleUI) return false

        return id == item.id
    }

}