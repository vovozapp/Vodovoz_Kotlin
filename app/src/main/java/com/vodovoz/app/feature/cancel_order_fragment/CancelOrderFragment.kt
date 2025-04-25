package com.vodovoz.app.feature.cancel_order_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.cancel_order_fragment.model.CancelOrderEvent
import com.vodovoz.app.feature.cancel_order_fragment.model.CancelOrderUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelOrderFragment : Fragment() {

    private val viewModel by viewModels<CancelOrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)

            setContent {
                VodovozTheme {
                    val viewState by viewModel.state.collectAsStateWithLifecycle()

                    when (viewState.uiState) {
                        CancelOrderUiState.Body -> {
                            CancelOrderScreen(
                                viewModel = viewModel,
                                viewState = viewState
                            )
                        }

                        CancelOrderUiState.Error -> {
                            NetworkErrorPlaceholder {
                                viewModel.fetchCancelOrderDetails()
                            }
                        }

                        CancelOrderUiState.Loading -> {
                            LoadingPlaceholder()
                        }
                    }

                    LifecycleEffect {
                        viewModel.events.collect { event ->
                            when (event) {
                                CancelOrderEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}