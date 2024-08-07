package com.vodovoz.app.feature.all.promotions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.AllPromotionBundleMapper.mapToUI
import com.vodovoz.app.ui.model.PromotionFilterUI
import com.vodovoz.app.ui.model.custom.AllPromotionBundleUI
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
class AllPromotionsFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
) : PagingStateViewModel<AllPromotionsFlowViewModel.AllPromotionsState>(AllPromotionsState()) {

    private var dataSource = savedState.get<AllPromotionsFragment.DataSource>("dataSource") ?: AllPromotionsFragment.DataSource.All

    private fun fetchAllPromotions(filterChanged: Boolean = false) {
        viewModelScope.launch {
            val dataSource = dataSource
            flow {
                when (dataSource) {
                    is AllPromotionsFragment.DataSource.All -> emit(
                        repository.fetchAllPromotions(
                            filterId = state.data.selectedFilterUi.id
                        )
                    )

                    is AllPromotionsFragment.DataSource.ByBanner -> emit(
                        repository.fetchPromotionsByBanner(
                            categoryId = dataSource.categoryId
                        )
                    )
                }
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                promotionFilterUIList = mutableListOf(
                                    PromotionFilterUI(
                                        id = 0,
                                        name = "Все акции",
                                        code = ""
                                    )
                                ).apply {
                                    addAll(data.promotionFilterUIList.toMutableList())
                                },
                                allPromotionBundleUI = data,
                                scrollToTop = filterChanged
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
                    debugLog { "fetch products by data source sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun updateBySelectedFilter(filterId: Long) {
        val filter = state.data.promotionFilterUIList.find { it.id == filterId } ?: return
        if(state.data.selectedFilterUi.id == filterId) return
        uiStateListener.value = state.copy(data = state.data.copy(selectedFilterUi = filter))
        fetchAllPromotions(true)
    }

    fun firstLoadSorted() {
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            fetchAllPromotions()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchAllPromotions()
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()
    fun updateScrollToTop() {
        if(state.data.scrollToTop) {
            uiStateListener.value = state.copy(data = state.data.copy(scrollToTop = false))
        }
    }

    data class AllPromotionsState(
        val promotionFilterUIList: List<PromotionFilterUI> = emptyList(),
        val allPromotionBundleUI: AllPromotionBundleUI? = null,
        val selectedFilterUi: PromotionFilterUI = PromotionFilterUI(
            id = 0,
            name = "Все акции",
            code = ""
        ),
        val scrollToTop: Boolean = false,
    ) : State
}