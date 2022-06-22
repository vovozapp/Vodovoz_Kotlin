package com.vodovoz.app.ui.components.fragment.fullScreenHistorySlider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vodovoz.app.databinding.FragmentFullscreenHistorySliderBinding
import com.vodovoz.app.ui.components.fragment.historyDetail.HistoryDetailFragment
import com.vodovoz.app.ui.model.HistoryUI

class FullScreenHistorySliderFragment : Fragment(), HistoryDetailFragment.IChangeHistory {

    private lateinit var binding: FragmentFullscreenHistorySliderBinding

    private lateinit var historyUIList: List<HistoryUI>
    private var startHistoryId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        FullScreenHistorySliderFragmentArgs.fromBundle(requireArguments()).let { args ->
            historyUIList = args.historyUIList.toList()
            startHistoryId = args.startHistoryId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFullscreenHistorySliderBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        initView()
    }.root

    private fun initView() {
        binding.historyPager.adapter = HistoryStateAdapter(
            fragment = this,
            iChangeHistory = this,
            historyUIList = historyUIList
        )
        binding.historyPager.currentItem = historyUIList.indexOfFirst { it.id == startHistoryId }
    }

    override fun nextHistory() {
        binding.historyPager.currentItem = binding.historyPager.currentItem + 1
    }

}