package com.vodovoz.app.feature.filters.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSliderState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.core.navigation.navigateToProductFilterValues
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.design_system.model.filters.FilterUi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductFiltersFlowFragment : Fragment() {

    private val viewModel: ProductFiltersFlowViewModel by viewModels()

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

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        findNavController().currentBackStackEntry?.savedStateHandle?.remove<FilterUi>("filter")
            ?.let { newFilter ->
                viewModel.changeFilter(newFilter)
            }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                VodovozTheme {
                    val pagingState by viewModel.observeUiState().collectAsStateWithLifecycle()
                    val viewState by rememberUpdatedState(newValue = pagingState.data)

                    val filterPrice = viewState.filters.price
                    val sliderState = remember(viewState.uiState) {
                        RangeSliderState(
                            activeRangeStart = (filterPrice.currentMin.toFloat() - filterPrice.min) / (filterPrice.max - filterPrice.min),
                            activeRangeEnd = (filterPrice.currentMax.toFloat() - filterPrice.min) / (filterPrice.max - filterPrice.min),
                            valueRange = 0f..1f,
                        )
                    }

                    when (viewState.uiState) {
                        ProductFiltersFlowViewModel.ProductFiltersUiState.Error -> {
                            NetworkErrorPlaceholder { viewModel.fetchFiltersByCategory() }
                        }

                        ProductFiltersFlowViewModel.ProductFiltersUiState.Loading -> {
                            LoadingPlaceholder()
                        }

                        ProductFiltersFlowViewModel.ProductFiltersUiState.Success -> {
                            ProductFiltersScreen(viewModel = viewModel, viewState = viewState, sliderState = sliderState)
                        }
                    }


                    LifecycleEffect(arg2 = sliderState) {
                        viewModel.observeEvent().collect { event ->
                            when (event) {
                                is ProductFiltersFlowViewModel.ProductFiltersEvent.GoBack -> {
                                    findNavController().popBackStack()
                                }

                                is ProductFiltersFlowViewModel.ProductFiltersEvent.GoToFilterValues -> {
                                    findNavController().navigateToProductFilterValues(
                                        event.categoryId,
                                        event.filter
                                    )
                                }

                                is ProductFiltersFlowViewModel.ProductFiltersEvent.GoToProductList -> {
                                    val navController = findNavController()

                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        "filters",
                                        event.filters
                                    ).also {
                                        navController.popBackStack()
                                    }
                                }

                                ProductFiltersFlowViewModel.ProductFiltersEvent.ResetSlider -> {
                                    sliderState.activeRangeStart = 0f
                                    sliderState.activeRangeEnd = 1f
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}