package com.vodovoz.app.feature.order_question_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.VodovozPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.VodovozLongPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.order_question_fragment.model.OrderQuestionEvent
import com.vodovoz.app.feature.order_question_fragment.model.OrderQuestionUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OrderQuestionFragment : Fragment() {

    private val viewModel by viewModels<OrderQuestionViewModel>()

    @Inject
    lateinit var tabManager: TabManager

    override fun onStart() {
        super.onStart()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabManager.changeTabVisibility(true)
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
                    val viewState by viewModel.state.collectAsStateWithLifecycle()
                    val snackbarHostState = remember { SnackbarHostState() }

                    when (val uiState = viewState.uiState) {
                        OrderQuestionUiState.Error -> {
                            NetworkErrorPlaceholder {
                                viewModel.fetchOrderQuestionDetails()
                            }
                        }

                        OrderQuestionUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        OrderQuestionUiState.Fields -> {
                            OrderQuestionScreen(
                                viewModel = viewModel,
                                viewState = viewState,
                                snackbarHostState = snackbarHostState
                            )
                        }

                        is OrderQuestionUiState.Success -> {
                            VodovozLongPlaceholder(
                                data = uiState.placeholderData,
                                onCloseClick = {
                                    viewModel.navigateBack()
                                },
                                onButtonClick = {
                                    viewModel.navigateBack()
                                }
                            )
                        }
                    }


                    LifecycleEffect(arg2 = snackbarHostState) {
                        viewModel.events.collect { event ->
                            when (event) {
                                OrderQuestionEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                is OrderQuestionEvent.ShowToast -> {
                                    snackbarHostState.showSnackbar(event.message)
                                }
                            }
                        }
                    }

                }
            }
        }
    }

}