package com.vodovoz.app.feature.catalog

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.catalog.CatalogManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.catalog.CatalogResponseJsonParser.parseCatalogResponse
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.stringToErrorState
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogFlowViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val catalogManager: CatalogManager
) : PagingStateViewModel<CatalogFlowViewModel.CatalogState>(CatalogState()) {

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchCatalog()
        }
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(loadingPage = true)
        fetchCatalog()
    }

    private fun fetchCatalog() {
        viewModelScope.launch {
            flow { emit(mainRepository.fetchCatalogResponse()) }
                .catch {
                    debugLog { "fetch catalog response error ${it.localizedMessage}" }

                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCatalogResponse()

                    when (response) {
                        is ResponseEntity.Hide -> {}
                        is ResponseEntity.Error -> state.copy(
                            error = response.errorMessage.stringToErrorState(),
                            loadingPage = false
                        )
                        is ResponseEntity.Success -> {
                            val catalog = response.data.mapToUI()
                            uiStateListener.value = state.copy(
                                loadingPage = false,
                                data = state.data.copy(itemsList = catalog),
                                error = null
                            )
                            catalogManager.saveCatalog(catalog)
                        }
                    }
                }
                .collect()
        }
    }

    data class CatalogState(
        val itemsList: List<CategoryUI> = emptyList()
    ) : State
}