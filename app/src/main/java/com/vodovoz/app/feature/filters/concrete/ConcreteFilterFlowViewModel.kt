package com.vodovoz.app.feature.filters.concrete

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
import com.vodovoz.app.design_system.model.filters.mapToUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.ui.model.custom.ConcreteFilterBundleUI
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConcreteFilterFlowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mainRepository: MainRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<ConcreteFilterFlowViewModel.ConcreteFilterState, ConcreteFilterFlowViewModel.ConcreteFilterEvent>(
    ConcreteFilterState()
) {

    private val filter = savedStateHandle.get<FilterUi>("filter")!!
    private val categoryId = savedStateHandle.get<Long>("categoryId")!!.toInt()

    init {
        fetchFilterValues()
    }


    private fun fetchFilterValues() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(uiState = ConcreteFilterUiState.Loading)
        }
        val filterValuesResult =
            vodovozServiceRepository.getFilterValues(categoryId, filter.id).singleResult()
        filterValuesResult.onSuccess { filterValues ->
            uiStateListener.updateData { s ->
                s.copy(
                    filter = filter.copy(
                        values = filterValues.mapToUi().map { value ->
                            value.copy(selected = filter.values.firstOrNull { it -> it.id == value.id && it.selected } != null)
                        }
                    ),
                    uiState = ConcreteFilterUiState.Success

                )
            }
        }.onFailure { t ->
            delay(300L)
            navigateBack()
        }
    }

    fun fetchProductFilterById() {

    }

    fun selectFilterValue(filterValue: FilterValueUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            val filter = s.filter
            s.copy(
                filter = filter.copy(
                    values = filter.values.toMutableList().apply {
                        set(
                            indexOf(filterValue),
                            filterValue.copy(selected = !filterValue.selected)
                        )
                    }
                ),
                showApplyButton = true
            )
        }
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(ConcreteFilterEvent.GoBack)
    }

    fun navigateToProductFilters() = viewModelScope.launch {
        val currentFilter = dataState.filter

        eventListener.emit(
            ConcreteFilterEvent.GoToProductFilters(
                filter = currentFilter.copy(
                    values = currentFilter.values
                )
            )
        )
    }

    fun changeSearchQuery(newSearchQuery: String) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(searchQuery = newSearchQuery)
        }
    }

    fun changeSearchMode(searchMode: Boolean) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                isSearchMode = searchMode,
                searchQuery = ""
            )
        }
    }

    @Immutable
    data class ConcreteFilterState(
        var concreteFilterBundleUI: ConcreteFilterBundleUI? = null,

        val filter: FilterUi = FilterUi.Empty,
        val searchQuery: String = "",
        val uiState: ConcreteFilterUiState = ConcreteFilterUiState.Loading,
        val showApplyButton: Boolean = false,
        val isSearchMode: Boolean = false
    ) : State {
    }

    @Immutable
    sealed interface ConcreteFilterUiState {
        data object Loading : ConcreteFilterUiState
        data object Success : ConcreteFilterUiState
    }

    sealed interface ConcreteFilterEvent : Event {
        data object GoBack : ConcreteFilterEvent
        data class GoToProductFilters(val filter: FilterUi) : ConcreteFilterEvent
    }

}