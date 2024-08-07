package com.vodovoz.app.feature.productlist.singleroot

import androidx.lifecycle.SavedStateHandle
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
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.custom.SingleRootCatalogBundleUI
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
class SingleRootCatalogFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val catalogManager: CatalogManager,
    savedStateHandle: SavedStateHandle,
) : PagingContractViewModel<SingleRootCatalogFlowViewModel.SingleRootState, SingleRootCatalogFlowViewModel.SingleRootEvents>(
    SingleRootState(
        selectedCategoryId = savedStateHandle.get<Long>("categoryId")
    )
) {

    fun fetchCategory() {
        viewModelScope.launch {
            val catalog = catalogManager.fetchCatalog()
            if (catalog.isNotEmpty()) {
                val root = getRoot(catalog)
                uiStateListener.value = state.copy(
                    loadingPage = false,
                    data = state.data.copy(
                        items = catalog,
                        rootCategory = root,
                        bundle = buildBundle(root, state.data.selectedCategoryId)
                    ),
                    error = null
                )
            } else {
                flow { emit(repository.fetchCatalogResponse()) }
                    .onEach { response ->
                        when (response) {
                            is ResponseEntity.Hide -> {}
                            is ResponseEntity.Error -> state.copy(
                                error = response.errorMessage.stringToErrorState(),
                                loadingPage = false
                            )
                            is ResponseEntity.Success -> {
                                val catalogFetched = response.data.mapToUI()
                                val root = getRoot(catalogFetched.categoryEntityList)
                                uiStateListener.value = state.copy(
                                    loadingPage = false,
                                    data = state.data.copy(
                                        items = catalogFetched.categoryEntityList,
                                        rootCategory = root,
                                        bundle = buildBundle(root, state.data.selectedCategoryId)
                                    ),
                                    error = null
                                )
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
    }

    private fun buildBundle(
        root: CategoryUI?,
        selectedId: Long? = null,
    ): SingleRootCatalogBundleUI? {
        val rootCat = root ?: return null
        val selected = selectedId ?: return null
        return SingleRootCatalogBundleUI(
            way = findWay(currentPoint = rootCat, endPointId = selected) ?: emptyList(),
            categoryUIList = listOf(rootCat)
        )
    }

    fun changeSelectedCategory(category: CategoryUI) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                selectedCategoryId = category.id,
                bundle = buildBundle(state.data.rootCategory, category.id)
            )
        )
    }

    private fun getRoot(categoryList: List<CategoryUI>): CategoryUI? {
        for (category in categoryList) {
            if (category.id == state.data.selectedCategoryId) return category
            if (findRoot(category.categoryUIList)) return category
        }
        return null
    }

    private fun findRoot(categoryList: List<CategoryUI>): Boolean {
        for (category in categoryList) {
            if (category.id == state.data.selectedCategoryId) return true
            if (findRoot(category.categoryUIList)) return true
        }
        return false
    }

    private fun findWay(
        way: MutableList<CategoryUI> = mutableListOf(),
        currentPoint: CategoryUI,
        endPointId: Long,
    ): List<CategoryUI>? {
        if (currentPoint.id == endPointId) {
            way.add(currentPoint)
            return way
        }

        if (currentPoint.categoryUIList.isEmpty()) return null

        for (category in currentPoint.categoryUIList) {
            val childWay = findWay(
                mutableListOf<CategoryUI>().apply {
                    addAll(way)
                    add(currentPoint)
                }, category, endPointId
            )

            when (childWay) {
                null -> continue
                else -> return childWay
            }
        }

        return null
    }

    fun navigateToCatalog() {
        viewModelScope.launch {
            val id = state.data.selectedCategoryId ?: return@launch
            eventListener.emit(SingleRootEvents.NavigateToCatalog(id))
        }
    }


    sealed class SingleRootEvents : Event {
        data class NavigateToCatalog(val id: Long) : SingleRootEvents()
    }

    data class SingleRootState(
        val items: List<CategoryUI>? = null,
        val rootCategory: CategoryUI? = null,
        val selectedCategoryId: Long? = null,
        val bundle: SingleRootCatalogBundleUI? = null,
    ) : State
}