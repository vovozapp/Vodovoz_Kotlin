package com.vodovoz.app.feature.filters.concrete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.mapToUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.custom.ConcreteFilterBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConcreteFilterFlowViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mainRepository: MainRepository,
) : PagingStateViewModel<ConcreteFilterFlowViewModel.ConcreteFilterState>(ConcreteFilterState()) {

    private val filterUI = savedStateHandle.get<FilterUI>("filter")
    private val categoryId = savedStateHandle.get<Long>("categoryId")

    fun fetchProductFilterById() {
        val filter = filterUI ?: return
        val id = categoryId ?: return
        viewModelScope.launch {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            flow {
                emit(
                    mainRepository
                        .fetchProductFilterById(
                            categoryId = id,
                            filterCode = filter.code
                        )
                )
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        response.data.mapToUI().let { data ->
                            validateData(data)
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    concreteFilterBundleUI = ConcreteFilterBundleUI(
                                        filterUI = filter,
                                        filterValueList = data
                                    )
                                ),
                                loadingPage = false,
                                error = null
                            )
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .catch {
                    debugLog { "fetch filters by id error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    private fun validateData(filterValueList: List<FilterValueUI>) {
        filterUI?.filterValueList?.forEach { customFilterValue ->
            filterValueList.find { it.id == customFilterValue.id }?.let {
                it.isSelected = customFilterValue.isSelected
            }
        }
    }

    fun prepareFilter(filterValueList: List<FilterValueUI>): FilterUI? {
        filterUI?.let { noNullFilter ->
            noNullFilter.filterValueList.clear()
            filterValueList.forEach { filterValue ->
                if (filterValue.isSelected) {
                    noNullFilter.filterValueList.add(filterValue)
                }
            }
        }
        return filterUI
    }

    data class ConcreteFilterState(
        var concreteFilterBundleUI: ConcreteFilterBundleUI? = null,
    ) : State

}