package com.vodovoz.app.ui.fragment.home.viewholders.homepromotions.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.PromotionUI

class HomePromotionsDiffUtilCallback: DiffUtil.ItemCallback<PromotionUI>() {

    override fun areContentsTheSame(oldItem: PromotionUI, newItem: PromotionUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: PromotionUI, newItem: PromotionUI): Boolean {
        return oldItem.id == newItem.id
    }
}