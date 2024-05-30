package com.vodovoz.app.feature.pastpurchases

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.favorite.mapper.FavoritesMapper
import com.vodovoz.app.mapper.PastPurchasesHeaderBundleMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.SortTypeUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PastPurchasesFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager,
) : PagingContractViewModel<PastPurchasesFlowViewModel.PastPurchasesState, PastPurchasesFlowViewModel.PastPurchasesEvents>(
    PastPurchasesState()
) {

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

    init {
        viewModelScope.launch {
            likeManager
                .observeLikes()
                .onEach {
                    if (!state.loadingPage) refreshSorted()
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchPastPurchasesHeader()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchPastPurchasesHeader()
    }

    private fun fetchPastPurchasesHeader() {
        val userId = accountManager.fetchAccountId()

        viewModelScope.launch {
            flow { emit(repository.fetchPastPurchasesHeader(userId = userId, isAvailable = state.data.isAvailable)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                favoriteCategory = checkSelectedFilter(data.favoriteCategoryUI),
                                availableTitle = data.availableTitle,
                                notAvailableTitle = data.notAvailableTitle,
                                emptyTitle = response.data.emptyTitle,
                                emptySubtitle = response.data.emptySubtitle,
                                sortType = data.favoriteCategoryUI?.sortTypeList?.sortTypeList?.firstOrNull { it.value == "default" }
                                    ?: SortTypeUI(sortName = "По популярности"),
                                scrollToTop = true,
                            ),
                            loadingPage = false,
                            error = null
                        )
                    } else {
                        uiStateListener.value =
                            state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "fetch past purchases error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun firstLoadSorted() {
        if (!state.data.isFirstLoadSorted) {
            uiStateListener.value =
                state.copy(data = state.data.copy(isFirstLoadSorted = true), loadingPage = true)
            fetchPastPurchasesSorted()
        }
    }

    fun refreshSorted() {
        uiStateListener.value = state.copy(
            loadingPage = true,
            page = 1,
            loadMore = false,
            bottomItem = null,
            data = state.data.copy(selectedCategoryId = -1)
        )
        fetchPastPurchasesHeader()
        fetchPastPurchasesSorted()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem())
            fetchPastPurchasesSorted()
        }
    }

    fun changeLayoutManager() {
        val manager = if (state.data.layoutManager == LINEAR) GRID else LINEAR
        uiStateListener.value = state.copy(
            data = state.data.copy(
                layoutManager = manager, itemsList = FavoritesMapper.mapFavoritesListByManager(
                    manager,
                    state.data.itemsList.filterIsInstance<ProductUI>()
                )
            )
        )
        changeLayoutManager.value = manager
    }

    private fun fetchPastPurchasesSorted() {
        val userId = accountManager.fetchAccountId()

        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchPastPurchasesProducts(
                        userId = userId,
                        categoryId = when (state.data.selectedCategoryId) {
                            -1L -> null
                            else -> state.data.selectedCategoryId
                        },
                        sort = state.data.sortType.value,
                        orientation = state.data.sortType.orientation,
                        isAvailable = state.data.isAvailable,
                        page = state.page
                    )
                )
            }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
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

                            val itemsList = if (state.loadMore) {
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
                    debugLog { "fetch past purchases sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
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

    fun isLoginAlready() = accountManager.isAlreadyLogin()

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

    fun changeRating(productId: Long, rating: Float, oldRating: Float) {
        viewModelScope.launch {
            ratingProductManager.rate(productId, rating = rating, oldRating = oldRating)
        }
    }

    fun updateByIsAvailable(bool: Boolean) {
        if (state.data.isAvailable == bool) return
        uiStateListener.value = state.copy(
            data = state.data.copy(isAvailable = bool),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchPastPurchasesHeader()
        fetchPastPurchasesSorted()
    }

    fun onTabClick(id: Long) {
        val categoryUI = state.data.favoriteCategory ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                favoriteCategory = categoryUI.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == id) }
                ),
                selectedCategoryId = id,
                scrollToTop = true,
            ),
            page = 1,
            loadMore = false
        )
        fetchPastPurchasesSorted()
    }

//    fun updateByCategory(categoryId: Long?) {
//        if (state.data.selectedCategoryId == categoryId) return
//        uiStateListener.value = state.copy(
//            data = state.data.copy(
//                selectedCategoryId = categoryId ?: -1,
//                sortType = SortTypeUI(),
//            ),
//            page = 1,
//            loadMore = false,
//            loadingPage = true
//        )
//        fetchPastPurchasesSorted()
//    }

    fun updateBySortType(sortType: SortTypeUI) {
        if (state.data.sortType == sortType) return
        val categoryUI = state.data.favoriteCategory
        uiStateListener.value = state.copy(
            data = state.data.copy(
                sortType = sortType,
                selectedCategoryId = -1,
                favoriteCategory = categoryUI?.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == -1L) }
                ),
                scrollToTop = true,
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchPastPurchasesSorted()
    }

    fun onPreOrderClick(id: Long, name: String, detailPicture: String) {
        viewModelScope.launch {
            val accountId = accountManager.fetchAccountId()
            if (accountId == null) {
                //eventListener.emit(PastPurchasesEvents.GoToProfile)
                eventListener.emit(PastPurchasesEvents.GoToPreOrder(id, name, detailPicture))
            } else {
                eventListener.emit(PastPurchasesEvents.GoToPreOrder(id, name, detailPicture))
            }
        }
    }

    fun clearScrollToTop()  {
        uiStateListener.value = state.copy(data = state.data.copy(scrollToTop  = false))
    }

    sealed class PastPurchasesEvents : Event {
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) :
            PastPurchasesEvents()

        data object GoToProfile : PastPurchasesEvents()
    }

    data class PastPurchasesState(
        val favoriteCategory: CategoryUI? = null,
        val availableTitle: String? = null,
        val notAvailableTitle: String? = null,
        val sortType: SortTypeUI = SortTypeUI(),
        val isAvailable: Boolean = true,
        val selectedCategoryId: Long = -1,
        val isFirstLoadSorted: Boolean = false,
        val itemsList: List<Item> = emptyList(),
        val layoutManager: String = LINEAR,
        val emptyTitle: String = "",
        val emptySubtitle: String = "",
        val scrollToTop: Boolean = false,
    ) : State

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}