package com.vodovoz.app.ui.fragment.home.viewholders.homebanners.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.BannerUI

class HomeBannersInnerDiffUtilCallback: DiffUtil.ItemCallback<BannerUI>() {

    override fun areContentsTheSame(oldItem: BannerUI, newItem: BannerUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: BannerUI, newItem: BannerUI): Boolean {
        return oldItem.id == newItem.id
    }
}