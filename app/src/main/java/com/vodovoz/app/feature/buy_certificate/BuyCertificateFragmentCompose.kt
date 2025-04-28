package com.vodovoz.app.feature.buy_certificate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.R
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToFAQ
import com.vodovoz.app.core.navigation.navigateToWebView
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.VodovozLongPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.util.extensions.openUrl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BuyCertificateFragment : Fragment() {

    private val viewModel by viewModels<BuyCertificateViewModel>()

    @Inject
    lateinit var tabManager: TabManager

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
    }

    override fun onStop() {
        super.onStop()
        tabManager.changeTabVisibility(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.viewModelScope.launch { delay(200L) }.invokeOnCompletion {
            viewModel.fetchBuyCertificateDetails()
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)

            setContent {
                VodovozTheme {
                    val pagingState by viewModel
                        .observeUiState()
                        .collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(pagingState.data)
                    val snackbarHostState = remember { SnackbarHostState() }

                    when (val uiState = viewState.uiState) {
                        BuyCertificateViewModel.BuyCertificateUiState.Body -> {
                            BuyCertificateScreen(
                                viewModel = viewModel,
                                viewState = viewState,
                                snackbarHostState = snackbarHostState
                            )
                        }

                        BuyCertificateViewModel.BuyCertificateUiState.Error -> {
                            NetworkErrorPlaceholder {
                                viewModel.fetchBuyCertificateDetails()
                            }
                        }

                        BuyCertificateViewModel.BuyCertificateUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        is BuyCertificateViewModel.BuyCertificateUiState.Success -> {
                            VodovozLongPlaceholder(
                                data = uiState.placeholder,
                                onCloseClick = { viewModel.navigateBack() },
                                onButtonClick = { viewModel.pay() }
                            )
                        }
                    }

                    LifecycleEffect {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                BuyCertificateViewModel.BuyCertificateEvents.AuthError -> {

                                }

                                BuyCertificateViewModel.BuyCertificateEvents.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                is BuyCertificateViewModel.BuyCertificateEvents.OpenLink -> {

                                }

                                is BuyCertificateViewModel.BuyCertificateEvents.OrderSuccess -> {

                                }

                                is BuyCertificateViewModel.BuyCertificateEvents.ShowPaymentMethod -> {

                                }

                                is BuyCertificateViewModel.BuyCertificateEvents.GoToFAQ -> {
                                    findNavController().navigateToFAQ(event.faq)
                                }

                                BuyCertificateViewModel.BuyCertificateEvents.GoToProfile -> {
                                    tabManager.setAuthRedirect(findNavController().graph.id)
                                    tabManager.selectTab(R.id.graph_profile)
                                }

                                is BuyCertificateViewModel.BuyCertificateEvents.GoToWebView -> {
                                    findNavController().navigateToWebView(
                                        event.url,
                                        requireContext().getString(R.string.space)
                                    )
                                }

                                is BuyCertificateViewModel.BuyCertificateEvents.OpenUrl -> {
                                    requireContext().openUrl(event.url)
                                }

                                is BuyCertificateViewModel.BuyCertificateEvents.ShowToast -> {
                                    launch { snackbarHostState.showSnackbar(event.message) }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

}