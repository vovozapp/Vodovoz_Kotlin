package com.vodovoz.app.ui.components.fragment.historySlider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.vodovoz.app.R
import com.vodovoz.app.databinding.FragmentSliderHistoryBinding
import com.vodovoz.app.ui.components.base.HorizontalMarginItemDecoration
import com.vodovoz.app.ui.components.base.VodovozApplication
import com.vodovoz.app.ui.components.adapter.historySliderAdapter.HistorySliderAdapter
import com.vodovoz.app.ui.components.diffUtils.HistoryDiffUtilCallback
import io.reactivex.rxjava3.subjects.PublishSubject


class HistorySliderFragment : Fragment() {

    companion object {
        fun newInstance(
            historySliderReadySubject: PublishSubject<Boolean>
        ) = HistorySliderFragment().apply {
            this.viewReadySubject = historySliderReadySubject
        }
    }

    private lateinit var viewReadySubject: PublishSubject<Boolean>

    private lateinit var binding: FragmentSliderHistoryBinding
    private lateinit var viewModel: HistorySliderViewModel

    private lateinit var historySliderAdapter: HistorySliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentSliderHistoryBinding.inflate(
        inflater,
        container,
        false
    ).apply {
        binding = this
        initHistoriesRecyclerView()
        initViewModel()
    }.root

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as VodovozApplication).viewModelFactory
        )[HistorySliderViewModel::class.java]
    }

    private fun initHistoriesRecyclerView() {
        binding.historiesRecycler.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        val space = resources.getDimension(R.dimen.primary_space).toInt()
        binding.historiesRecycler.addItemDecoration(HorizontalMarginItemDecoration(space))

        binding.historiesRecycler.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (binding.historiesRecycler.width != 0) {
                        binding.historiesRecycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        historySliderAdapter = HistorySliderAdapter((binding.historiesRecycler.width - (space * 4))/3)
                        binding.historiesRecycler.adapter = historySliderAdapter
                        observeViewModel()
                    }
                }
            }
        )
    }

    private fun observeViewModel() {
        viewModel.historyListLD.observe(viewLifecycleOwner) { historyUIDataList ->
            val diffUtil = HistoryDiffUtilCallback(
                oldList = historySliderAdapter.historyUIList,
                newList = historyUIDataList
            )

            DiffUtil.calculateDiff(diffUtil).let { diffResult ->
                historySliderAdapter.historyUIList = historyUIDataList
                diffResult.dispatchUpdatesTo(historySliderAdapter)
            }

            viewReadySubject.onNext(true)
        }

        viewModel.sateHideLD.observe(viewLifecycleOwner) { stateHide ->
            when(stateHide) {
                true -> binding.root.visibility = View.VISIBLE
                false -> binding.root.visibility = View.GONE
            }
        }
    }

}