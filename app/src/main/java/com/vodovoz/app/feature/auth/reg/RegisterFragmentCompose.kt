package com.vodovoz.app.feature.auth.reg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToWebView
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.auth.reg.composables.RegisterScreen
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val viewModel: RegFlowViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by viewModels()

    @Inject
    lateinit var tabManager: TabManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState = pagingState.data
                    val snackbarHostState = remember { SnackbarHostState() }

                    RegisterScreen(
                        viewModel = viewModel,
                        viewState = viewState,
                        snackbarHostState = snackbarHostState
                    )

                    DisposableEffect(Unit) {
                        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
                        tabManager.changeTabVisibility(false)
                        onDispose {
                            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
                            tabManager.changeTabVisibility(true)

                        }
                    }

                    LifecycleEffect {
                        observeEvents(this, snackbarHostState)
                    }
                }
            }
        }
    }

    private suspend fun observeEvents(
        coroutineScope: CoroutineScope,
        snackbarHostState: SnackbarHostState
    ) {
        viewModel.observeEvent().collect { event ->
            when (event) {
                is RegFlowViewModel.RegEvents.RegError -> {
                }

                is RegFlowViewModel.RegEvents.RegSuccess -> {
                    findNavController().popBackStack()
                }

                is RegFlowViewModel.RegEvents.ShowSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(event.message)
                    }
                }

                RegFlowViewModel.RegEvents.GoBack -> {
                    findNavController().popBackStack()
                }

                RegFlowViewModel.RegEvents.GoToProfile -> {
                    profileViewModel.fetchProfileDetails()
                    findNavController().popBackStack(
                        R.id.profileFragment,
                        false
                    )
                }

                is RegFlowViewModel.RegEvents.GoToWebView -> {
                    findNavController().navigateToWebView(event.url, event.title)
                }
            }
        }

    }

}