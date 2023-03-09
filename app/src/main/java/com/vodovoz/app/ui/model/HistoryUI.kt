package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.ui.base.content.itemadapter.Item
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryUI(
    val id: Long,
    val detailPicture: String,
    val bannerUIList: List<BannerUI>
) : Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_history
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is HistoryUI) return false

        return id == item.id
    }
}