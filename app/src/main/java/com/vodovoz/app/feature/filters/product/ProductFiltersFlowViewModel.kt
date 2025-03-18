package com.vodovoz.app.feature.filters.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.filters.FiltersUi
import com.vodovoz.app.design_system.model.filters.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.mapper.FilterBundleMapper.mapToUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ProductFiltersFlowViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MainRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<ProductFiltersFlowViewModel.ProductFiltersState, ProductFiltersFlowViewModel.ProductFiltersEvent>(
    ProductFiltersState()
) {

    private val categoryId = savedStateHandle.get<Long>("categoryId")?.toInt()

    init {
        fetchFiltersByCategory()
    }


    fun fetchFiltersByCategory() = viewModelScope.launch {

        uiStateListener.updateData { s ->
            s.copy(uiState = ProductFiltersUiState.Loading)
        }

        val id = savedStateHandle.get<Long>("categoryId")?.toInt() ?: categoryId ?: -1
        val filtersResult = vodovozServiceRepository.getFilters(id).singleResult()
        filtersResult.onSuccess { filters ->
            uiStateListener.updateData { s ->
                s.copy(
                    filters = filters.toUi(),
                    uiState = ProductFiltersUiState.Success
                )
            }
        }.onFailure {
            uiStateListener.updateData { s ->
                s.copy(uiState = ProductFiltersUiState.Error)
            }
        }
    }



    private fun mergeFiltersBundles(
        filterBundle: FiltersBundleUI?,
        defaultBundle: FiltersBundleUI?,
    ) {
        filterBundle?.let { noNullCustomFilterBundle ->
            defaultBundle?.let { noNullDefaultFilterBundle ->
                noNullCustomFilterBundle.filterUIList.forEach { customFilter ->
                    noNullDefaultFilterBundle.filterUIList.find { filter ->
                        filter.code == customFilter.code
                    }?.filterValueList = customFilter.filterValueList
                }
            }
        }
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(ProductFiltersEvent.GoBack)
    }

    fun changeFiltersPrice(range: ClosedFloatingPointRange<Float>) = viewModelScope.launch {
        uiStateListener.updateData { s ->

            val filtersPrice = s.filters.price
            val delta = filtersPrice.max - filtersPrice.min
            s.copy(
                filters = s.filters.copy(
                    price = filtersPrice.copy(
                        currentMin = filtersPrice.min + (delta * range.start).roundToInt(),
                        currentMax = filtersPrice.min + (delta * range.endInclusive).roundToInt()
                    )
                )
            )
        }
    }

    data class ProductFiltersState(
        val filterBundle: FiltersBundleUI? = null,
        val defaultBundle: FiltersBundleUI? = null,

        val filters: FiltersUi = FiltersUi.Empty,
        val uiState: ProductFiltersUiState = ProductFiltersUiState.Loading,
    ) : State

    sealed interface ProductFiltersUiState {
        data object Loading : ProductFiltersUiState
        data object Success : ProductFiltersUiState
        data object Error : ProductFiltersUiState
    }

    sealed interface ProductFiltersEvent : Event {
        data object GoBack : ProductFiltersEvent
    }
}