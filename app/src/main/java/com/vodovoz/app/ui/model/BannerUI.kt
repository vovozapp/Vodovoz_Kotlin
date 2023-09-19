package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.model.common.ActionEntity
import com.vodovoz.app.feature.home.viewholders.homebanners.model.BannerAdvEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class BannerUI(
    val id: Long,
    val detailPicture: String,
    val actionEntity: ActionEntity? = null,
    val advEntity: BannerAdvEntity? = null
): Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_slider_banner
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is BannerUI) return false

        return id == item.id
    }

}