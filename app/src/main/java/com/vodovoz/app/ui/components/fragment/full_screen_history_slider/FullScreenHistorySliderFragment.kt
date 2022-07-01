package com.vodovoz.app.ui.components.fragment.full_screen_history_slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.databinding.FragmentFullscreenHistorySliderBinding
import com.vodovoz.app.ui.components.fragment.history_detail.HistoryDetailFragment
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
            startHistoryId = args.startHistoryId
            historyUIList = args.historyList.toList()
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
        binding = this
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
        if (binding.historyPager.currentItem == historyUIList.indices.last) {
            findNavController().popBackStack()
        } else {
            binding.historyPager.currentItem = binding.historyPager.currentItem + 1
        }
    }

}