package com.vodovoz.app.feature.certificate_activation

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
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.certificate_activation.composables.CertificateActivatedPlaceholder
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationEvent
import com.vodovoz.app.feature.certificate_activation.model.CertificateActivationUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CertificateActivationFragment : Fragment() {

    internal val viewModel: CertificateActivationViewModel by viewModels()

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
                            }

                        }
                    }

                }
            }
        }
    }

}