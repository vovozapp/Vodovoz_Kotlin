package com.vodovoz.app.feature.full_screen_history_slider.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vodovoz.app.feature.history_detail.HistoryDetailFragment
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
