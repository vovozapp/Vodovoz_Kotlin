package com.vodovoz.app.ui.fragment.home.viewholders.homehistories.inneradapter

import androidx.recyclerview.widget.DiffUtil
import com.vodovoz.app.ui.model.HistoryUI

class HomeHistoriesInnerDiffUtilCallback: DiffUtil.ItemCallback<HistoryUI>() {

    override fun areContentsTheSame(oldItem: HistoryUI, newItem: HistoryUI): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: HistoryUI, newItem: HistoryUI): Boolean {
        return oldItem.id == newItem.id
    }
}