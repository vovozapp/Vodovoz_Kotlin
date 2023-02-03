package com.vodovoz.app.ui.fragment.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.vodovoz.app.R
import com.vodovoz.app.ui.base.BaseFragment
import com.vodovoz.app.ui.base.ErrorState
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.fragment.catalog.CatalogViewModel
import com.vodovoz.app.ui.fragment.home.HomeFlowViewModel
import com.vodovoz.app.ui.fragment.home.HomeViewModel
import com.vodovoz.app.ui.fragment.main.MainFragment
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    override fun layout(): Int = R.layout.fragment_splash

    private val viewModel: HomeViewModel by activityViewModels()
    private val flowViewModel: HomeFlowViewModel by activityViewModels()
    private val catalogViewModel: CatalogViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewModel.firstLoad()
        flowViewModel.firstLoad()
        catalogViewModel.firstLoad()
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
                    if (state.data.items.size == HomeFlowViewModel.POSITIONS_COUNT) {
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