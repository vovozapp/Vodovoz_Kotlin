package com.vodovoz.app.feature.all.brands

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.design_system.model.BrandUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.ui.model.BrandUI
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllBrandsFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
//    private val dataRepository: DataRepository,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<AllBrandsFlowViewModel.AllBrandsState, AllBrandsFlowViewModel.AllBrandsEvents>(
    AllBrandsState()
) {

    private var dataSource = savedState.get<LongArray>("brandIdList")

    private fun fetchAllBrands() = viewModelScope.launch {

        val brandsFlow =
            vodovozServiceRepository.getBrandsPaged(dataState.searchQuery).map { pagingData ->
                pagingData.map { brand -> brand.toUi() }
            }


        vodovozServiceRepository.getBrands(dataState.searchQuery).singleResult()
            .onSuccess { brandSectionModel ->
                uiStateListener.updateData { s ->
                    s.copy(
                        uiState = AllBrandsUiState.Success,
                        title = brandSectionModel.title,
                        brands = brandsFlow
                    )
                }
            }.onFailure {
                navigateBack()
            }

    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(AllBrandsEvents.GoBack)
    }

    fun firstLoadSorted() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            fetchAllBrands()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchAllBrands()
    }

    fun filterByQuery(query: String) {
        val newList = if (query.isNotBlank()) {
            state.data.items.filter { it.name.contains(query, ignoreCase = true) }
        } else {
            state.data.items
        }
        if (newList == state.data.filteredItems) return
        uiStateListener.value = state.copy(
            data = state.data.copy(
                filteredItems = newList,
                scrollToTop = true
            )
        )
    }

    fun updateScrollToTop() {
        if (state.data.scrollToTop) {
            uiStateListener.value = state.copy(data = state.data.copy(scrollToTop = false))
        }
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()

    fun changeSearchMode(searchMode: Boolean) = viewModelScope.launch {
        if (!searchMode) searchQueriesStateFlow.value = ""
        uiStateListener.updateData { s ->
            s.copy(isSearchMode = searchMode)
        }
    }

    @OptIn(FlowPreview::class)
    private val searchQueriesStateFlow = MutableStateFlow("").apply {
        drop(1).onEach { newSearchQuery ->
            uiStateListener.updateData { s -> s.copy(searchQuery = newSearchQuery) }
        }.debounce(200).onEach { _ ->
            fetchAllBrands()
        }.launchIn(viewModelScope)
    }

    fun changeSearchQuery(newSearchQuery: String) = viewModelScope.launch {
        searchQueriesStateFlow.value = newSearchQuery
    }

    fun navigateToBrandProducts(brandId: Long) = viewModelScope.launch {
        eventListener.emit(AllBrandsEvents.GoToBrandProducts(brandId))
    }

    @Immutable
    data class AllBrandsState(
        val items: List<BrandUI> = emptyList(),
        val filteredItems: List<BrandUI> = emptyList(),
        val scrollToTop: Boolean = false,

        val brands: Flow<PagingData<BrandUi>> = emptyFlow(),
        val title: String = "",
        val searchQuery: String = "",
        val uiState: AllBrandsUiState = AllBrandsUiState.Loading,
        val isSearchMode: Boolean = false,
    ) : State

    sealed class AllBrandsEvents : Event {
        data class GoToBrandProducts(val brandId: Long) : AllBrandsEvents()
        data object GoBack : AllBrandsEvents()
    }

    sealed interface AllBrandsUiState {
        data object Loading : AllBrandsUiState
        data object Success : AllBrandsUiState
    }
}