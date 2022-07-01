package com.vodovoz.app.ui.components.fragment.full_screen_history_slider

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vodovoz.app.ui.components.fragment.history_detail.HistoryDetailFragment
import com.vodovoz.app.ui.model.HistoryUI

class HistoryStateAdapter(
    fragment: Fragment,
    private val iChangeHistory: HistoryDetailFragment.IChangeHistory,
    private val historyUIList: List<HistoryUI>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = historyUIList.size

    override fun createFragment(position: Int) = HistoryDetailFragment.newInstance(
        iChangeHistory = iChangeHistory,
        historyUI = historyUIList[position]
    )

}