package com.vodovoz.app.feature.certificate_activation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToWebView
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.certificate_activation.composables.CertificateActivatedPlaceholder
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationEvent
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CertificateActivationFragment : Fragment() {

    internal val viewModel: CertificateActivationViewModel by viewModels()
    @Inject
    internal lateinit var tabManager: TabManager

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
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val viewState by viewModel.state.collectAsStateWithLifecycle()
                    when (val uiState = viewState.uiState) {

                        is CertificateActivationUiState.CertificateActivated -> {
                            CertificateActivatedPlaceholder(
                                message = uiState.message,
                                onCloseClick = {
                                    viewModel.navigateBack()
                                },
                                onOkClick = {
                                    viewModel.navigateBack()
                                }
                            )
                        }

                        CertificateActivationUiState.Error -> {
                            NetworkErrorPlaceholder {
                                viewModel.fetchCertificateActivationDetails()
                            }
                        }

                        else -> {
                            CertificateActivationScreen(
                                viewModel = viewModel,
                                viewState = viewState
                            )
                        }
                    }

                    LifecycleEffect {
                        viewModel.events.collect { event ->
                            when (event) {
                                CertificateActivationEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                is CertificateActivationEvent.GoToWebView -> {
                                    findNavController().navigateToWebView(event.url, "")
                                }
                            }

                        }
                    }

                }
            }
        }
    }

}