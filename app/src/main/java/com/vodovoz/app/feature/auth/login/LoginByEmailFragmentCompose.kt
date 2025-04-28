package com.vodovoz.app.feature.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToRecoverPassword
import com.vodovoz.app.core.navigation.navigateToRegister
import com.vodovoz.app.core.navigation.navigateToWebView
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.auth.login.composables.LoginByEmailUiState
import com.vodovoz.app.feature.auth.login.model.LoginByEmailEvent
import com.vodovoz.app.feature.cart.CartFlowViewModel
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.HomeFlowViewModel
import com.vodovoz.app.feature.profile.ProfileFlowViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginByEmailFragment : Fragment() {

    @Inject
    lateinit var tabManager: TabManager

    @Inject
    lateinit var accountManager: AccountManager

    private val viewModel: LoginByEmailViewModel by viewModels()
    private val profileViewModel: ProfileFlowViewModel by activityViewModels()
    private val homeViewModel: HomeFlowViewModel by activityViewModels()
    private val cartFlowViewModel: CartFlowViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteFlowViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
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

                    when (viewState.uiState) {
                        LoginByEmailUiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchLoginByEmailDetails() }
                        }

                        LoginByEmailUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        LoginByEmailUiState.Success -> {
                            LoginByEmailScreen(viewModel = viewModel, viewState = viewState)
                        }
                    }

                    LifecycleEffect {
                        viewModel.events.collect { event ->
                            when (event) {
                                LoginByEmailEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                LoginByEmailEvent.GoToRegister -> {
                                    findNavController().navigateToRegister()
                                }

                                is LoginByEmailEvent.GoToWebView -> {
                                    findNavController().navigateToWebView(event.url, event.title)
                                }

                                LoginByEmailEvent.RefreshAll -> {
                                    profileViewModel.refresh()
                                    homeViewModel.refresh()
                                    cartFlowViewModel.refresh()
                                    favoriteViewModel.refresh()

                                    val redirect = tabManager.fetchAuthRedirect()
                                    if (redirect == TabManager.DEFAULT_AUTH_REDIRECT) {
                                        findNavController().popBackStack(
                                            R.id.profileFragment, false
                                        )
                                    } else {
                                        findNavController().popBackStack(
                                            R.id.profileFragment, false
                                        )
                                        tabManager.selectTab(redirect)
                                        tabManager.setDefaultAuthRedirect()
                                    }
                                }

                                LoginByEmailEvent.GoToRecoverPassword -> {
                                    findNavController().navigateToRecoverPassword()
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
