package com.vodovoz.app.feature.catalog

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.catalog.CatalogManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.stringToErrorState
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.CatalogBannerUI
import com.vodovoz.app.ui.model.CategoryUI
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
class CatalogFlowViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val catalogManager: CatalogManager,
) : PagingContractViewModel<CatalogFlowViewModel.CatalogState, CatalogFlowViewModel.CatalogEvents>(
    CatalogState()
) {

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
                .onEach { response ->
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
                                data = state.data.copy(
                                    itemsList = catalog.categoryEntityList,
                                    topCatalogBanner = catalog.topCatalogBanner
                                ),
                                error = null
                            )
                            catalogManager.saveCatalog(catalog.categoryEntityList)
                        }
                    }
                }
                .catch {
                    debugLog { "fetch catalog response error ${it.localizedMessage}" }

                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun goToProfile() {
        viewModelScope.launch {
            eventListener.emit(CatalogEvents.GoToProfile)
        }
    }

    sealed class CatalogEvents : Event {
        object GoToProfile : CatalogEvents()
    }

    data class CatalogState(
        val itemsList: List<CategoryUI> = emptyList(),
        val topCatalogBanner: CatalogBannerUI? = null,
    ) : State
}