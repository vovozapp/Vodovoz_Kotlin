package com.vodovoz.app.feature.home.ratebottom

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.FragmentRateBottomBinding
import com.vodovoz.app.feature.home.ratebottom.adapter.RateBottomImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RateBottomFragment : BaseBottomSheetFragment() {

    override fun layout(): Int {
        return R.layout.fragment_rate_bottom
    }

    private val binding: FragmentRateBottomBinding by viewBinding {
        FragmentRateBottomBinding.bind(contentView)
    }

    private val viewModel: RateBottomViewModel by activityViewModels()

    private val collapsedImagesAdapter = RateBottomImageAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindErrorRefresh { viewModel.refresh() }
        observeUiState()
        initImageRv()
    }

    private fun initImageRv() {
        with(binding.collapsedRv) {
            adapter = collapsedImagesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .observeUiState()
                .collect { state ->
                    if (state.loadingPage) {
                        showLoader()
                    } else {
                        hideLoader()
                    }

                    if (state.data.expandedData != null) {
                        binding.expandedLL.isVisible = true
                        binding.collapsedLL.isVisible = false
                    } else {
                        binding.expandedLL.isVisible = false
                        binding.collapsedLL.isVisible = true
                    }

                    if (state.data.collapsedData != null) {

                        binding.collapsedBodyTv.text = state.data.collapsedData.body
                        binding.collapsedHeaderTv.text = state.data.collapsedData.title
                        if (!state.data.collapsedData.imageList.isNullOrEmpty()) {
                            collapsedImagesAdapter.submitList(state.data.collapsedData.imageList)
                        }

                        binding.expandedLL.isVisible = false
                        binding.collapsedLL.isVisible = true
                    } else {
                        binding.expandedLL.isVisible = true
                        binding.collapsedLL.isVisible = false
                    }

                    showError(state.error)
                }
        }
    }
}