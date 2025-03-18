package com.vodovoz.app.feature.filters.product

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
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.ui.model.FilterUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFiltersFlowFragment : Fragment() {

    companion object {
        const val CONCRETE_FILTER = "CONCRETE_FILTER"
    }

    private val viewModel: ProductFiltersFlowViewModel by viewModels()

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

                    when(viewState.uiState){
                        ProductFiltersFlowViewModel.ProductFiltersUiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchFiltersByCategory() }
                        }
                        ProductFiltersFlowViewModel.ProductFiltersUiState.Loading -> {
                            LoadingPlaceholder()
                        }
                        ProductFiltersFlowViewModel.ProductFiltersUiState.Success -> {
                            ProductFiltersScreen(viewModel = viewModel, viewState = viewState)
                        }
                    }


                    LifecycleEffect {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                ProductFiltersFlowViewModel.ProductFiltersEvent.GoBack -> {
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