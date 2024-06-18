package com.vodovoz.app.feature.filters.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.FilterBundleMapper.mapToUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
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
class ProductFiltersFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
) : PagingStateViewModel<ProductFiltersFlowViewModel.ProductFiltersState>(ProductFiltersState()) {

    private val filterBundle = savedState.get<FiltersBundleUI>("defaultFiltersBundle")
    private val categoryId = savedState.get<Long>("categoryId")

    fun fetchAllFiltersByCategory() {
        val id = categoryId ?: return
        viewModelScope.launch {
            uiStateListener.value = state.copy(
                loadingPage = true,
                error = null
            )
            flow { emit(repository.fetchAllFiltersByCategory(id)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val defaultBundle = response.data.mapToUI()
                        val filterBundle = filterBundle
                        mergeFiltersBundles(filterBundle, defaultBundle)
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                defaultBundle = defaultBundle,
                                filterBundle = filterBundle
                            ),
//                            loadingPage = false,
                            error = null
                        )
                    } else {
                        uiStateListener.value =
                            state.copy(
//                                loadingPage = false,
                                error = ErrorState.Error()
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "fetch all filters by category error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState()/*, loadingPage = false*/)
                }
                .collect()
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

    fun changeConcreteFilter(
        concreteFilter: FilterUI,
        customFilterBundle: FiltersBundleUI?,
        defaultBundle: FiltersBundleUI?,
    ) {
        if (concreteFilter.filterValueList.isNotEmpty()) {
            customFilterBundle?.let { noNullCustomFilterBundle ->
                when (val index =
                    noNullCustomFilterBundle.filterUIList.indexOfFirst { it.code == concreteFilter.code }) {
                    -1 -> {
                        noNullCustomFilterBundle.filterUIList.add(concreteFilter)
                    }
                    else -> {
                        noNullCustomFilterBundle.filterUIList[index] = concreteFilter
                    }
                }
            }
            mergeFiltersBundles(customFilterBundle, defaultBundle)
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    defaultBundle = defaultBundle,
                    filterBundle = customFilterBundle
                ),
//                loadingPage = false,
                error = null
            )
        }
    }

    fun recyclerReady(isReady: Boolean) {
        uiStateListener.value = state.copy(loadingPage = !isReady)
    }

    data class ProductFiltersState(
        val filterBundle: FiltersBundleUI? = null,
        val defaultBundle: FiltersBundleUI? = null,
    ) : State
}