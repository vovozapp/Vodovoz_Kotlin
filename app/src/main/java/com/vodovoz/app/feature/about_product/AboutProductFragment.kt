package com.vodovoz.app.feature.about_product

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
import com.vodovoz.app.design_system.VodovozTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutProductFragment : Fragment() {

    internal val viewModel: AboutProductViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel.loadAboutProductInfo()

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                val viewState by viewModel.state.collectAsStateWithLifecycle()

                VodovozTheme {

                    AboutProductScreen(
                        viewModel = viewModel,
                        viewState = viewState
                    )
                }
            }
        }
    }

}