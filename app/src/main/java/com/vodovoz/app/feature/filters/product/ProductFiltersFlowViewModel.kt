package com.vodovoz.app.feature.filters.product

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.design_system.model.filters.FilterUi
import com.vodovoz.app.design_system.model.filters.FilterValueUi
import com.vodovoz.app.design_system.model.filters.FiltersPriceUi
import com.vodovoz.app.design_system.model.filters.FiltersUi
import com.vodovoz.app.design_system.model.filters.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@HiltViewModel
class ProductFiltersFlowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MainRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<ProductFiltersFlowViewModel.ProductFiltersState, ProductFiltersFlowViewModel.ProductFiltersEvent>(
    ProductFiltersState()
) {

    private val categoryIdArg =
        savedStateHandle.remove<Long>("categoryId")?.toInt() ?: navigateBack().let { -1 }
    private val filtersArg = savedStateHandle.remove<FiltersUi>("filters") ?: FiltersUi.Empty

    init {
        listenFiltersSelectionState()
        fetchFiltersByCategory()
    }

    private fun listenFiltersSelectionState() = viewModelScope.launch {
        uiStateListener.map { it.data.filters }.collectLatest { filters ->
            val anyFilterHasSelected = filters.filters.any { filter ->
                filter.values.any { it.selected }
            }
            val filtersPrice = filters.price

            uiStateListener.updateData { s ->
                s.copy(
                    showClearButton = anyFilterHasSelected || filtersPrice.currentMin != filtersPrice.min || filtersPrice.currentMax != filtersPrice.max
                )
            }
        }
    }

    fun clearFilters() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val filters = s.filters
            val price = filters.price

            s.copy(
                filters = filters.copy(
                    filters = filters.filters.map { filter ->
                        filter.copy(values = filter.values.map { it.copy(selected = false) })
                    },
                    price = filters.price.copy(
                        currentMax = price.max,
                        currentMin = price.min
                    )
                ),
            )
        }
        eventListener.emit(ProductFiltersEvent.ResetSlider)
    }


    fun fetchFiltersByCategory() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(uiState = ProductFiltersUiState.Loading)
        }

        val filtersResult = vodovozServiceRepository.getFilters(categoryIdArg).singleResult()
        filtersResult.onSuccess { filters ->
            uiStateListener.updateData { s ->
                s.copy(
                    filters = mergeFilters(filtersArg, filters.toUi()),
                    uiState = ProductFiltersUiState.Success
                )
            }
        }.onFailure {
            uiStateListener.updateData { s ->
                s.copy(uiState = ProductFiltersUiState.Error)
            }
        }
    }


    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(ProductFiltersEvent.GoBack)
    }

    fun changePriceFromField(min: String) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val price = s.filters.price
            s.copy(
                filters = s.filters.copy(
                    price = price.copy(
                        currentMin = min.toIntOrNull() ?: price.currentMin
                    )
                )
            )
        }
    }

    fun changePriceToField(max: String) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val price = s.filters.price
            s.copy(
                filters = s.filters.copy(
                    price = price.copy(
                        currentMax = max.toIntOrNull() ?: price.currentMax
                    )
                )
            )
        }
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
                ),
                showApplyButton = true,
            )
        }
    }


    fun selectFilterValue(filter: FilterUi, filterValue: FilterValueUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val filters = s.filters
            s.copy(
                filters = filters.copy(
                    filters = s.filters.filters.map { currentFilter ->
                        if (currentFilter == filter) {
                            val updatedValues = currentFilter.values.mapNotNull { currentValue ->
                                if (currentValue == filterValue && currentValue.selected && currentFilter.values.size > 6) null
                                else if (currentValue == filterValue) currentValue.copy(selected = !currentValue.selected)
                                else currentValue
                            }.sortedWith(filterValueComparator)
                            currentFilter.copy(values = updatedValues)
                        } else currentFilter
                    }
                ),
                showApplyButton = true
            )
        }
    }

    fun navigateToFilterValues(filter: FilterUi) = viewModelScope.launch {
        eventListener.emit(ProductFiltersEvent.GoToFilterValues(filter, categoryIdArg.toLong()))
    }

    fun navigateToProductList() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val price = s.filters.price
            s.copy(
                filters = s.filters.copy(
                    price = price.copy(
                        currentMin = price.currentMin.coerceIn(price.min, price.max),
                        currentMax = price.currentMax.coerceIn(price.min, price.max)
                    )
                )
            )
        }
        eventListener.emit(ProductFiltersEvent.GoToProductList(dataState.filters))
    }


    fun changeFilter(newFilter: FilterUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val filters = s.filters
            s.copy(
                filters = filters.copy(
                    filters = mergeFiltersWithPriority(
                        originalFilters = filters.filters,
                        priorityFilters = listOf(newFilter)
                    )
                ),
                showApplyButton = true
            )
        }
    }

    private val filterValueComparator: Comparator<FilterValueUi> =
        compareByDescending<FilterValueUi> { it -> it.selected }.thenBy {
            if (!it.selected) it.name.toDoubleOrNull() ?: it.name else ""
        }


    private fun mergeFilters(
        filters: FiltersUi,
        secondFilters: FiltersUi,
    ): FiltersUi {
        val price = filters.price
        val secondPrice = secondFilters.price
        val newMin = min(price.min, secondPrice.min)
        val newMax = max(price.max, secondPrice.max)

        val newCurrentMin = listOf(price.currentMin, secondPrice.currentMin)
            .filter { it -> it in newMin..newMax && it != newMin }.maxOrNull() ?: newMin
        val newCurrentMax = listOf(price.currentMax, secondPrice.currentMax)
            .filter { it -> it in newMin..newMax && it != newMax }.maxOrNull() ?: newMax

        return filters.copy(
            filters = mergeFilters(filters.filters, secondFilters.filters),
            price = FiltersPriceUi(
                min = newMin,
                max = newMax,
                currentMin = newCurrentMin,
                currentMax = newCurrentMax
            )
        )
    }

    private fun mergeFilters(
        originalFilters: List<FilterUi>,
        newFilters: List<FilterUi>,
    ): List<FilterUi> {
        val allFilters = originalFilters + newFilters
        return allFilters.groupBy { filter -> filter.id }.mapNotNull { idAndFilters ->
            val currentFilter = idAndFilters.value.firstOrNull() ?: return@mapNotNull null
            val filterValues = idAndFilters.value.map { it -> it.values }.flatten()
                .sortedWith(filterValueComparator).distinctBy { it -> it.id }

            currentFilter.copy(
                values = filterValues.filterIndexed { index, filterValue ->
                    filterValue.selected || index < 6
                }

            )
        }
    }

    private fun mergeFiltersWithPriority(
        originalFilters: List<FilterUi>,
        priorityFilters: List<FilterUi>,
    ): List<FilterUi> {
        val allFilters = originalFilters + priorityFilters
        return allFilters.groupBy { filter -> filter.id }.mapNotNull { idAndFilters ->
            val currentFilter = idAndFilters.value.firstOrNull() ?: return@mapNotNull null
            val filterValues = idAndFilters.value
                .asSequence()
                .map { it.values }
                .flatten().groupBy { it.id }
                .mapNotNull { idAndValues ->
                    val values = idAndValues.value.distinctBy { it.selected }
                    values.lastOrNull()
                }.sortedWith(compareByDescending { it.selected })
                .toList()


            currentFilter.copy(
                values = filterValues.filterIndexed { index, filterValue ->
                    filterValue.selected || index < 6
                }

            )
        }
    }


    @Immutable
    data class ProductFiltersState(
        val filterBundle: FiltersBundleUI? = null,
        val defaultBundle: FiltersBundleUI? = null,

        val filters: FiltersUi = FiltersUi.Empty,
        val uiState: ProductFiltersUiState = ProductFiltersUiState.Loading,
        val showApplyButton: Boolean = false,
        val showClearButton: Boolean = false,
    ) : State

    @Immutable
    sealed interface ProductFiltersUiState {
        data object Loading : ProductFiltersUiState
        data object Success : ProductFiltersUiState
        data object Error : ProductFiltersUiState
    }

    @Immutable
    sealed interface ProductFiltersEvent : Event {
        data object GoBack : ProductFiltersEvent
        data class GoToFilterValues(val filter: FilterUi, val categoryId: Long) :
            ProductFiltersEvent

        data class GoToProductList(val filters: FiltersUi) :
            ProductFiltersEvent

        data object ResetSlider: ProductFiltersEvent
    }
}