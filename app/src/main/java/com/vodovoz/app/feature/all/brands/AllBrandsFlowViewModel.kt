package com.vodovoz.app.feature.all.brands

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.ui.model.BrandUI
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
class AllBrandsFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
//    private val dataRepository: DataRepository,
    private val accountManager: AccountManager,
) : PagingStateViewModel<AllBrandsFlowViewModel.AllBrandsState>(AllBrandsState()) {

    private var dataSource = savedState.get<LongArray>("brandIdList")

    private fun fetchAllBrands() {
        viewModelScope.launch {

            val list = dataSource?.toList() ?: emptyList()

            flow { emit(repository.fetchAllBrands(list)) }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                items = data,
                                filteredItems = data
                            ),
                            loadingPage = false,
                            error = null
                        )

                    } else {
                        uiStateListener.value =
                            state.copy(
                                loadingPage = false,
                                error = ErrorState.Error(),
                                page = 1,
                                loadMore = false
                            )
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "fetch all brands error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
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
        val newList = if (query.isNotBlank() ) {
            state.data.items.filter { it.name.contains(query, ignoreCase = true) }
        } else {
            state.data.items
        }
        if(newList == state.data.filteredItems) return
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

    data class AllBrandsState(
        val items: List<BrandUI> = emptyList(),
        val filteredItems: List<BrandUI> = emptyList(),
        val scrollToTop: Boolean = false,
    ) : State
}