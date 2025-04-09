package com.vodovoz.app.feature.products_collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.vodovoz.app.core.navigation.navigateToAnalogs
import com.vodovoz.app.core.navigation.navigateToProductDetails
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.bottom_sheet.SortOptionsBottomSheet
import com.vodovoz.app.design_system.effects.LifecycleEffect
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsCollectionFragment : Fragment() {

    private val viewModel: ProductsCollectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchProducts()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                val viewState by viewModel.state.collectAsStateWithLifecycle()

                VodovozTheme {
                    ProductAnalogsScreen(viewModel = viewModel, viewState = viewState)

                    if (viewState.showSortOptionsBottomSheet) {
                        SortOptionsBottomSheet(
                            onDismissRequest = { viewModel.closeSortOptionsBottomSheet() },
                            currentSort = viewState.currentSort,
                            sorting = viewState.productsSection.sorting,
                            onSortSelect = { sort ->
                                viewModel.selectSort(sort)
                            }
                        )
                    }

                    LifecycleEffect {
                        viewModel.listenCart()
                    }

                    LifecycleEffect {
                        viewModel.listenFavorites()
                    }

                    LifecycleEffect {
                        viewModel.listenProductLoadings()
                    }

                }

                LifecycleEffect {
                    viewModel.events.collect { event ->
                        when (event) {
                            ProductsCollectionEvent.GoBack -> {
                                findNavController().popBackStack()
                            }

                            is ProductsCollectionEvent.GoToProductAnalogs -> {
                                findNavController().navigateToProductDetails(event.productId)
                            }

                            is ProductsCollectionEvent.GoToProductDetails -> {
                                findNavController().navigateToAnalogs(event.productId)
                            }
                        }
                    }
                }
            }
        }
    }

}