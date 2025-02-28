package com.vodovoz.app.feature.preorder

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.core.navigation.tryNavigate
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@AndroidEntryPoint
class PreOrderFragment : Fragment() {

    private val viewModel: PreOrderFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchPreOrderData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val viewState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewStateData = viewState.data
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val snackbarHostState = remember {
                        SnackbarHostState()
                    }

                    when (viewStateData.uiState) {
                        PreOrderFlowViewModel.UiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchPreOrderData() }
                        }

                        PreOrderFlowViewModel.UiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        PreOrderFlowViewModel.UiState.Success -> {
                            PreOrderScreen(
                                viewModel = viewModel,
                                viewState = viewStateData,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }

                    LifecycleEffect {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                PreOrderFlowViewModel.PreOrderEvent.GoBack -> {
                                    findNavController().navigateUp()
                                }

                                PreOrderFlowViewModel.PreOrderEvent.HideKeyboard -> {
                                    keyboardController?.hide()
                                }

                                is PreOrderFlowViewModel.PreOrderEvent.ShowSnackbar -> {
                                    withTimeoutOrNull(if (event.isVeryShort) 150L else 900L) {
                                        snackbarHostState.showSnackbar(
                                            message = event.message,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}