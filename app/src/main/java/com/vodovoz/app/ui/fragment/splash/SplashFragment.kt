package com.vodovoz.app.ui.fragment.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.vodovoz.app.R
import com.vodovoz.app.common.content.BaseFragment
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.home.HomeViewModel
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.feature.catalog.CatalogFlowViewModel
import com.vodovoz.app.ui.fragment.main.MainFragment
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_splash

    private val viewModel: HomeViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val catalogViewModel: CatalogFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel.firstLoad()
        flowViewModel.firstLoad()
        catalogViewModel.firstLoad()
        cartFlowViewModel.firstLoad()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //observeViewModel()
        observeFlowViewModel()
    }

    private fun observeFlowViewModel() {
        lifecycleScope.launchWhenStarted {
            flowViewModel.observeUiState()
                .collect { state ->
                    debugLog {
                        "${state.data.items.map { it.position }}"
                    }
                    if (state.data.items.size in (HomeFlowViewModel.POSITIONS_COUNT - 2..HomeFlowViewModel.POSITIONS_COUNT)) {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fcvMainContainer, MainFragment())
                            .commit()
                    }
                    if (state.error is ErrorState.NetworkError) {
                        showError(state.error)
                    }
                }
        }
    }

    private fun observeViewModel() {
        viewModel.viewStateLD.observe(viewLifecycleOwner) { state ->
            when(state) {
                is ViewState.Hide -> {}
                is ViewState.Loading -> {}
                is ViewState.Error -> {}
                is ViewState.Success -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fcvMainContainer, MainFragment())
                        .commit()
                }
            }
        }
    }
}