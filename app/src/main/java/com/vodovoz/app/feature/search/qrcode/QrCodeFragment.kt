package com.vodovoz.app.feature.search.qrcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.util.extensions.disableFullScreen
import com.vodovoz.app.util.extensions.snack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QrCodeFragment : Fragment() {


    private val viewModel: QrCodeViewModel by viewModels()


    @Inject
    lateinit var tabManager: TabManager

    override fun onStart() {
        super.onStart()
        tabManager.changeTabVisibility(false)
        requireActivity().enableEdgeToEdge()
    }

    override fun onStop() {
        super.onStop()
        tabManager.changeTabVisibility(true)
        requireActivity().disableFullScreen()

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
                    val dataState = viewState.data

                    ScannerScreen(
                        viewState = dataState,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeEvent()
                    .collect { qrCodeEvents ->
                        when (qrCodeEvents) {
                            is QrCodeViewModel.QrCodeEvents.Success -> {
                                findNavController().navigate(
                                    QrCodeFragmentDirections.actionToProductDetailFragment(
                                        qrCodeEvents.id.toLong()
                                    )
                                )
                            }

                            is QrCodeViewModel.QrCodeEvents.Error -> {
                                requireActivity().snack(qrCodeEvents.message)
                            }

                            QrCodeViewModel.QrCodeEvents.GoBack -> {
                                findNavController().popBackStack()
                            }
                        }
                    }
            }

        }
    }

}