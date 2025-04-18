package com.vodovoz.app.feature.cart.bottles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AllBottlesFlowFragment : Fragment() {

    internal val viewModel: AllBottlesFlowViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
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
            setContent {
                setViewCompositionStrategy(ViewCompositionStrategy.Default)

                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(pagingState.data)


                    Crossfade(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        targetState = viewState.uiState,
                        label = "all bottles screen crossfade"
                    ) { state ->
                        when (state) {
                            AllBottlesFlowViewModel.BottlesUiState.Error -> NetworkErrorPlaceholder {
                                viewModel.fetchAllBottlesDetails()
                            }

                            AllBottlesFlowViewModel.BottlesUiState.Loading -> LoadingPlaceholder()
                            AllBottlesFlowViewModel.BottlesUiState.Success -> AllBottlesScreen(
                                viewModel = viewModel,
                                viewState = viewState
                            )
                        }
                    }

                    LifecycleEffect {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                AllBottlesFlowViewModel.BottlesEvent.GoBack -> {
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