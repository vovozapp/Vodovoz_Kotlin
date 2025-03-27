package com.vodovoz.app.feature.profile.change_password

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import com.vodovoz.app.feature.profile.change_password.composables.PasswordChangedPlaceholder
import com.vodovoz.app.feature.profile.change_password.model.ChangePasswordEvent
import com.vodovoz.app.feature.profile.change_password.model.ChangePasswordUiState
import com.vodovoz.app.util.extensions.disableFullScreen
import com.vodovoz.app.util.extensions.enableFullScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    val viewModel: ChangePasswordViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()

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
                    val viewState by viewModel.state.collectAsStateWithLifecycle()
                    val snackbarHostState = remember {
                        SnackbarHostState()
                    }

                    when (viewState.uiState) {
                        ChangePasswordUiState.ChangePassword -> {
                            ChangePasswordScreen(
                                viewModel = viewModel,
                                viewState = viewState,
                                snackbarHostState = snackbarHostState
                            )
                        }

                        ChangePasswordUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        ChangePasswordUiState.PasswordChanged -> {
                            PasswordChangedPlaceholder(
                                onCloseClick = { viewModel.navigateBack() },
                                onFineClick = { viewModel.navigateBack() }
                            )
                        }
                    }

                    LifecycleEffect(arg2 = snackbarHostState) {
                        viewModel.events.collect { event ->
                            when (event) {
                                ChangePasswordEvent.GoBack -> findNavController().popBackStack()
                                ChangePasswordEvent.Logout -> {
                                    profileViewModel.logout()
                                    findNavController().popBackStack()
                                }

                                is ChangePasswordEvent.ShowSnackbar -> {
                                    launch {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                        snackbarHostState.showSnackbar(event.message)
                                    }
                                }
                            }
                        }
                    }



                    DisposableEffect(Unit) {
                        tabManager.changeTabVisibility(false)
                        requireActivity().enableFullScreen()
                        onDispose {
                            requireActivity().disableFullScreen()
                            tabManager.changeTabVisibility(true)
                        }
                    }


                }
            }
        }
    }

}