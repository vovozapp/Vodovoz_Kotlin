package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.common.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceUI(
    val name: String,
    val detail: String? = null,
    val price: String? = null,
    val detailPicture: String? = null,
    val type: String
): Parcelable, Item {

    override fun getItemViewType(): Int {
        return SERVICE_VIEW_TYPE
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is ServiceUI) return false

        return this == item
    }

    companion object {
        const val SERVICE_VIEW_TYPE = -41241244
    }
}