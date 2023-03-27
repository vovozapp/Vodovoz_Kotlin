package com.vodovoz.app.ui.fragment.favorite

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.data.parser.response.favorite.FavoriteHeaderResponseJsonParser.parseFavoriteProductsHeaderBundleResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.FavoriteProductsResponseJsonParser.parseFavoriteProductsResponse
import com.vodovoz.app.mapper.FavoriteProductsHeaderBundleMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : PagingStateViewModel<FavoriteFlowViewModel.FavoriteState>(FavoriteState()){

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchFavoriteProductsHeader()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchFavoriteProductsHeader()
    }

    private fun fetchFavoriteProductsHeader() {
        val userId = localDataSource.fetchUserId()

        viewModelScope.launch {
            flow { emit(repository.fetchFavoriteProducts(userId = userId, null)) }
                .catch {
                    debugLog { "fetch favorite products error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseFavoriteProductsHeaderBundleResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                favoriteCategory = checkSelectedFilter(data.favoriteCategoryUI),
                                bestForYouCategoryDetailUI = data.bestForYouCategoryDetailUI,
                                availableTitle = data.availableTitle,
                                notAvailableTitle = data.notAvailableTitle
                            )
                        )
                    } else {
                        uiStateListener.value =
                            state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun firstLoadSorted() {
        if (!state.data.isFirstLoadSorted) {
            uiStateListener.value = state.copy(data = state.data.copy(isFirstLoadSorted = true), loadingPage = true)
            fetchFavoriteProductsSorted()
        }
    }

    fun refreshSorted() {
        uiStateListener.value = state.copy(loadingPage = true, data = state.data.copy(page = 1))
        fetchFavoriteProductsSorted()
    }

    fun loadMoreSorted() {

    }

    fun changeLayoutManager() {
        val manager = if (state.data.layoutManager == LINEAR) GRID else LINEAR
        uiStateListener.value = state.copy(data = state.data.copy(layoutManager = manager))
    }

    private fun fetchFavoriteProductsSorted() {
        val userId = localDataSource.fetchUserId()

        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchFavoriteProductsSorted(
                        userId = userId,
                        categoryId = when(state.data.selectedCategoryId) {
                            -1L -> null
                            else -> state.data.selectedCategoryId
                        },
                        sort = state.data.sortType.value,
                        orientation = state.data.sortType.orientation,
                        isAvailable = state.data.isAvailable,
                        page = state.data.page,
                        productIdListStr = ""
                    )
                )
            }
                .catch {
                    debugLog { "fetch favorite products sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseFavoriteProductsResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()



                    } else {
                        uiStateListener.value =
                            state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    private fun checkSelectedFilter(categoryUI: CategoryUI?): CategoryUI? {
        if (categoryUI == null) return null

        if (categoryUI.categoryUIList.isNotEmpty()) {
            categoryUI.categoryUIList = categoryUI.categoryUIList.toMutableList().apply {
                add(
                    0, CategoryUI(
                        id = -1,
                        name = "Все",
                        isSelected = true
                    )
                )
            }
        }
        return categoryUI
    }

    fun onTabClick(id: Long) {
        val categoryUI = state.data.favoriteCategory ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                favoriteCategory = categoryUI.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == id) }
                ),
                selectedCategoryId = id
            )
        )
    }

    fun isLoginAlready() = dataRepository.isAlreadyLogin()

    fun changeCart(productId: Long, quantity: Int, oldQuan: Int) {
        viewModelScope.launch {
            cartManager.add(id = productId, oldCount = oldQuan, newCount = quantity)
        }
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            likeManager.like(productId, !isFavorite)
        }
    }

    fun updateByIsAvailable(bool: Boolean) {
        if (state.data.isAvailable == bool) return
        uiStateListener.value = state.copy(data = state.data.copy(isAvailable = bool, page = 1, loadMoreSorted = false))
        fetchFavoriteProductsSorted()
    }

    fun updateByCategory(categoryId: Long?) {
        if (state.data.selectedCategoryId == categoryId) return
        uiStateListener.value = state.copy(data = state.data.copy(selectedCategoryId = categoryId ?: -1, page = 1, loadMoreSorted = false))
        fetchFavoriteProductsSorted()
    }

    fun updateBySortType(sortType: SortType) {
        if (state.data.sortType == sortType) return
        uiStateListener.value = state.copy(data = state.data.copy(sortType = sortType, page = 1, loadMoreSorted = false))
        fetchFavoriteProductsSorted()
    }

    data class FavoriteState(
        val favoriteCategory: CategoryUI? = null,
        val bestForYouCategoryDetailUI: CategoryDetailUI? = null,
        val availableTitle: String? = null,
        val notAvailableTitle: String? = null,
        val sortType: SortType = SortType.NO_SORT,
        val isAvailable: Boolean = true,
        val page: Int = 1,
        val isFirstLoadSorted: Boolean = false,
        val loadMoreSorted: Boolean = false,
        val selectedCategoryId: Long = -1,
        val itemsList: List<Item> = emptyList(),
        val layoutManager: String = LINEAR
    ) : State

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}