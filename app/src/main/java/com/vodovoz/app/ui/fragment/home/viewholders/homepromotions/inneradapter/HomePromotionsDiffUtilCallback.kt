package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.BannerUI
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsDiffUtilCallback(
    private val oldItems: List<PromotionUI>,
    private val newItems: List<PromotionUI>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].id == newItems[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}