package com.vodovoz.app.ui.fragment.full_screen_history_slider

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.vodovoz.app.ui.fragment.history_detail.HistoryDetailFragment
import com.vodovoz.app.ui.model.HistoryUI

class HistoriesDetailStateAdapter(
    fragment: Fragment,
    private val historyUIList: List<HistoryUI>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = historyUIList.size

    override fun createFragment(position: Int) = HistoryDetailFragment.newInstance(
        historyUI = historyUIList[position]
    )

}
