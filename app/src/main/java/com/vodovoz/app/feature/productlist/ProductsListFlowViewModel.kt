package com.vodovoz.app.feature.productlist

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.data.parser.response.favorite.FavoriteHeaderResponseJsonParser.parseFavoriteProductsHeaderBundleResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.FavoriteProductsResponseJsonParser.parseFavoriteProductsResponse
import com.vodovoz.app.feature.favorite.FavoritesMapper
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
class ProductsListFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager
) : PagingStateViewModel<ProductsListFlowViewModel.ProductsListState>(ProductsListState()){

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

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
                                notAvailableTitle = data.notAvailableTitle,
                            ),
                            loadingPage = false
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
        uiStateListener.value = state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchFavoriteProductsHeader()
        fetchFavoriteProductsSorted()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem())
            fetchFavoriteProductsSorted()
        }
    }

    fun changeLayoutManager() {
        val manager = if (state.data.layoutManager == LINEAR) GRID else LINEAR
        uiStateListener.value = state.copy(data = state.data.copy(layoutManager = manager, itemsList = FavoritesMapper.mapFavoritesListByManager(
            manager,
            state.data.itemsList.filterIsInstance<ProductUI>()
        )
        ))
        changeLayoutManager.value = manager
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
                        page = state.page,
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
                        val mappedFeed = FavoritesMapper.mapFavoritesListByManager(
                            state.data.layoutManager,
                            data
                        )

                        uiStateListener.value = if (data.isEmpty() && !state.loadMore) {
                            state.copy(
                                error = ErrorState.Empty(),
                                loadingPage = false,
                                loadMore = false,
                                bottomItem = null,
                                page = 1
                            )
                        } else {

                            val itemsList =  if (state.loadMore) {
                                state.data.itemsList + mappedFeed
                            } else {
                                mappedFeed
                            }

                            state.copy(
                                page = if (mappedFeed.isEmpty()) null else state.page?.plus(1),
                                loadingPage = false,
                                data = state.data.copy(itemsList = itemsList),
                                error = null,
                                loadMore = false,
                                bottomItem = null
                            )
                        }

                    } else {
                        uiStateListener.value =
                            state.copy(loadingPage = false, error = ErrorState.Error(), page = 1, loadMore = false)
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
        uiStateListener.value = state.copy(data = state.data.copy(isAvailable = bool), page = 1, loadMore = false, loadingPage = true)
        fetchFavoriteProductsSorted()
    }

    fun onTabClick(id: Long) {
        val categoryUI = state.data.favoriteCategory ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                favoriteCategory = categoryUI.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == id) }
                ),
                selectedCategoryId = id,
                sortType = SortType.NO_SORT
            ),
            page = 1,
            loadMore = false
        )
        fetchFavoriteProductsSorted()
    }

    fun updateByCategory(categoryId: Long?) {
        if (state.data.selectedCategoryId == categoryId) return
        uiStateListener.value = state.copy(
            data = state.data.copy(
                selectedCategoryId = categoryId ?: -1,
                sortType = SortType.NO_SORT,
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchFavoriteProductsSorted()
    }

    fun updateBySortType(sortType: SortType) {
        if (state.data.sortType == sortType) return
        val categoryUI = state.data.favoriteCategory
        uiStateListener.value = state.copy(
            data = state.data.copy(
                sortType = sortType,
                selectedCategoryId = -1,
                favoriteCategory = categoryUI?.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == -1L) }
                )
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchFavoriteProductsSorted()
    }

    data class ProductsListState(
        val favoriteCategory: CategoryUI? = null,
        val bestForYouCategoryDetailUI: CategoryDetailUI? = null,
        val availableTitle: String? = null,
        val notAvailableTitle: String? = null,
        val sortType: SortType = SortType.NO_SORT,
        val isAvailable: Boolean = true,
        val selectedCategoryId: Long = -1,
        val isFirstLoadSorted: Boolean = false,
        val itemsList: List<Item> = emptyList(),
        val layoutManager: String = LINEAR
    ) : State

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}