package com.vodovoz.app.feature.catalog

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.catalog.CatalogManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.stringToErrorState
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.design_system.model.BannerUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi
import com.vodovoz.app.feature.catalog.model.toUi
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.CatalogBannerUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogFlowViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val catalogManager: CatalogManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<CatalogFlowViewModel.CatalogState, CatalogFlowViewModel.CatalogEvents>(
    CatalogState()
) {

    fun firstLoad() {
        fetchCatalogDetails()
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchCatalogOld()
        }
    }

    fun refresh() {
        uiStateListener.value =
            state.copy(loadingPage = true)
        fetchCatalogOld()
    }

    private fun fetchCatalogDetails() = viewModelScope.launch {
        vodovozServiceRepository.getCatalogDetails().collect { catalogDetailsResult ->
            catalogDetailsResult.onSuccess { catalogDetails ->
                val (banners, categories) = catalogDetails.toUi()
                uiStateListener.updateData { s ->
                    s.copy(
                        categories = categories,
                        banners = banners,
                        uiState = UiState.Success
                    )
                }
            }.onFailure {
                uiStateListener.updateData { s ->
                    s.copy(uiState = UiState.Error)
                }
            }
        }
    }

    private fun fetchCatalogOld() {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun navigateToSearch() = viewModelScope.launch {
        eventListener.emit(CatalogEvents.GoToSearch)
    }

    fun navigateToSubCategories(catalogCategory: CatalogCategoryUi) = viewModelScope.launch {
        if (catalogCategory.childCategories.isNotEmpty()) {
            eventListener.emit(CatalogEvents.GoToSubCategories(catalogCategory))
        } else {

        }
    }

    sealed class CatalogEvents : Event {
        data class GoToSubCategories(val catalogCategory: CatalogCategoryUi) : CatalogEvents()

        data object GoToProfile : CatalogEvents()
        data object GoToSearch : CatalogEvents()
    }

    data class CatalogState(
        val itemsList: List<CategoryUI> = emptyList(),
        val topCatalogBanner: CatalogBannerUI? = null,

        val categories: List<CatalogCategoryUi> = emptyList(),
        val banners: List<BannerUi> = emptyList(),
        val uiState: UiState = UiState.Success,
    ) : State

    sealed interface UiState {
        data object Success : UiState
        data object Error : UiState
    }
}