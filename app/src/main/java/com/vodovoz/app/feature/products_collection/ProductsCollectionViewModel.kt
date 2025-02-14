package com.vodovoz.app.feature.products_collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.domain.general.model.toDomain
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toDomain
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionEvent
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionState
import com.vodovoz.app.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsCollectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : MviViewModel<ProductsCollectionState, ProductsCollectionEvent>(
    ProductsCollectionState()
) {
    private val productId = savedStateHandle.get<Long>("productId") ?: -1

    fun fetchProducts() = viewModelScope.launch {
        vodovozServiceRepository.getProductAnalogs(productId, stateSnapshot.currentSort.toDomain())
            .onEach { result ->

                result.onSuccess { productsSectionModel ->
                    val productsSectionUi = productsSectionModel.toDomain()
                    _state.update { s ->
                        s.copy(
                            productsSection = productsSectionUi,
                            currentSort = if (s.currentSort == SortUi.Empty) productsSectionUi.sorting.firstOrNull()
                                ?: SortUi(
                                    productsSectionUi.sortingTitle,
                                    "",
                                    ""
                                ) else s.currentSort
                        )
                    }
                }.onFailure {

                }
            }.collect()
    }

    fun showSortOptionsBottomSheet() = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                showSortOptionsBottomSheet = true
            )
        }
    }

    fun closeSortOptionsBottomSheet() = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                showSortOptionsBottomSheet = false
            )
        }
    }

    fun selectSort(sort: SortUi) = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                currentSort = sort,
            )
        }
        fetchProducts()
        delay(50L)
        closeSortOptionsBottomSheet()
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(ProductsCollectionEvent.GoBack)
    }

    fun switchLayoutView() = viewModelScope.launch {
        _state.update { s ->
            s.copy(isGridView = !s.isGridView)
        }
    }


}