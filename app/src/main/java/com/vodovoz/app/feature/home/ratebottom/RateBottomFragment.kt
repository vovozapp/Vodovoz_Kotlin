package com.vodovoz.app.feature.home.ratebottom

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseBottomSheetFragment
import com.vodovoz.app.databinding.FragmentRateBottomBinding
import com.vodovoz.app.util.extensions.fromHtml
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindErrorRefresh { viewModel.refresh() }
        observeUiState()

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

                    showError(state.error)
                }
        }
    }
}