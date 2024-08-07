package com.vodovoz.app.feature.productlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.catalog.CatalogManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.config.FiltersConfig
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.favorite.mapper.FavoritesMapper
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.FilterUI
import com.vodovoz.app.ui.model.FilterValueUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.SortTypeUI
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterQuery
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterRangeQuery
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterValueQuery
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsListFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val catalogManager: CatalogManager,
    private val ratingProductManager: RatingProductManager,
) : PagingStateViewModel<ProductsListFlowViewModel.ProductsListState>(ProductsListState()) {

    private var categoryId = savedState.get<Long>("categoryId") ?: 0

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

//    private fun fetchCategoryHeader() {
//        viewModelScope.launch {
//            flow { emit(repository.fetchCategoryHeader(categoryId)) }
//                .flowOn(Dispatchers.IO)
//                .onEach { response ->
//                    if (response is ResponseEntity.Success) {
//                        val data = response.data.mapToUI()
//
//                        uiStateListener.value = state.copy(
//                            data = state.data.copy(
//                                categoryHeader = data,
//                                categoryId = categoryId,
//                                showCategoryContainer = catalogManager.hasRootItems(categoryId),
//                                filterCode = data.filterCode.ifEmpty {
//                                    state.data.filterCode
//                                },
//                                sortType = data.sortTypeList?.sortTypeList?.firstOrNull { it.value == "default" }
//                                    ?: SortTypeUI(sortName = "По популярности")
//                            ),
//                            loadingPage = false,
//                            error = null
//                        )
//                    } else {
//                        uiStateListener.value =
//                            state.copy(loadingPage = false, error = ErrorState.Error())
//                    }
//                }
//                .flowOn(Dispatchers.Default)
//                .catch {
//                    debugLog { "fetch category header error ${it.localizedMessage}" }
//                    uiStateListener.value =
//                        state.copy(error = it.toErrorState(), loadingPage = false)
//                }
//                .collect()
//        }
//    }

    private fun fetchProductsByCategory() {
        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchProductsByCategory(
                        categoryId = categoryId, // ?: return@flow,
                        sort = state.data.sortType.value,
                        orientation = state.data.sortType.orientation,
                        filter = state.data.filterBundle.filterUIList.buildFilterQuery(),
                        filterValue = state.data.filterBundle.filterUIList.buildFilterValueQuery(),
                        priceFrom = state.data.filterBundle.filterPriceUI.minPrice,
                        priceTo = state.data.filterBundle.filterPriceUI.maxPrice,
                        page = state.page,
                        filterMap = state.data.filterBundle.filterUIList.buildFilterRangeQuery()
                    )
                )
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        val mappedFeed = FavoritesMapper.mapFavoritesListByManager(
                            state.data.layoutManager,
                            data.productList
                        )

                        uiStateListener.value = if (data.productList.isEmpty() && !state.loadMore) {
                            state.copy(
                                error = ErrorState.Empty(),
                                data = state.data.copy(
                                    itemsList = emptyList(),
                                    categoryHeader = if(state.page == 1) data else state.data.categoryHeader,
                                ),
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
                                data = state.data.copy(
                                    itemsList = itemsList,
                                    categoryHeader = if(state.page == 1) data else state.data.categoryHeader,
                                    categoryId = categoryId,
                                    showCategoryContainer = catalogManager.hasRootItems(categoryId),
                                    filterCode = data.filterCode.ifEmpty {
                                        state.data.filterCode
                                    },
                                    sortType = data.sortTypeList?.sortTypeList?.firstOrNull { it.value == state.data.sortType.value && it.orientation == state.data.sortType.orientation }
                                        ?: SortTypeUI(sortName = "По популярности", value = "default"),
                                    scrollToTop = state.page == 1,
                                ),
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
                    debugLog { "fetch products by category sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchProductsByCategory()
        }
    }

//    fun refresh() {
//        uiStateListener.value = state.copy(loadingPage = true)
//        fetchProductsByCategory()
//    }

//    fun firstLoadSorted() {
//        if (!state.data.isFirstLoadSorted) {
//            uiStateListener.value =
//                state.copy(data = state.data.copy(isFirstLoadSorted = true), loadingPage = true)
//            fetchProductsByCategory()
//        }
//    }

    fun clearScrollState() {
        uiStateListener.value = state.copy(data = state.data.copy(scrollToTop = false))
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
//        fetchCategoryHeader()
        fetchProductsByCategory()
    }

    fun loadMoreSorted() {
        state.data.categoryHeader?.let {
            if (it.limit < it.totalCount) {
                if (state.bottomItem == null && state.page != null) {
                    uiStateListener.value =
                        state.copy(loadMore = true, bottomItem = BottomProgressItem(), data = state.data.copy(scrollToTop = false))
                    fetchProductsByCategory()
                }
            }
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

    fun updateByCat(categoryId: Long) {
        this.categoryId = categoryId
        uiStateListener.value = state.copy(
            data = state.data.copy(
                filterBundle = FiltersBundleUI(),
                filtersAmount = fetchFiltersAmount(FiltersBundleUI()),
                categoryId = categoryId
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
//        fetchCategoryHeader()
        fetchProductsByCategory()
    }

    fun updateBySortType(sortType: SortTypeUI) {
        if (state.data.sortType == sortType) return
        val categoryUI = state.data.categoryHeader
        uiStateListener.value = state.copy(
            data = state.data.copy(
                sortType = sortType,
                categoryHeader = categoryUI?.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == -1L) }
                )
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchProductsByCategory()
    }


    fun updateFilterBundle(filterBundle: FiltersBundleUI) {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                filterBundle = filterBundle,
                filtersAmount = fetchFiltersAmount(filterBundle)
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
//        fetchCategoryHeader()
        fetchProductsByCategory()
    }

    fun addPrimaryFilterValue(filterValue: FilterValueUI) {

        val categoryUI = state.data.categoryHeader ?: return

        val bundle = state.data.filterBundle
        bundle.filterUIList.removeAll { it.code == state.data.filterCode }
        bundle.filterUIList.add(
            FilterUI(
                code = state.data.filterCode,
                name = FiltersConfig.BRAND_FILTER_NAME,
                filterValueList = mutableListOf(filterValue)
            )
        )
        uiStateListener.value = state.copy(
            data = state.data.copy(
                filterBundle = bundle,
                filtersAmount = fetchFiltersAmount(bundle),
                categoryHeader = categoryUI.copy(
                    primaryFilterValueList = categoryUI.primaryFilterValueList.map {
                        it.copy(
                            isSelected = it.id == filterValue.id
                        )
                    }
                )
            ),
            page = 1,
            loadMore = false,
            loadingPage = true,
            bottomItem = null
        )
        fetchProductsByCategory()
    }

    private fun fetchFiltersAmount(filterBundle: FiltersBundleUI): Int {
        var filtersAmount = 0
        if (filterBundle.filterPriceUI.minPrice != Int.MIN_VALUE
            || filterBundle.filterPriceUI.maxPrice != Int.MAX_VALUE
        ) {
            filtersAmount++
        }

        filtersAmount += filterBundle.filterUIList.size

        return filtersAmount
    }

    data class ProductsListState(
        val categoryId: Long = 0,
        val categoryHeader: CategoryUI? = null,
        val filterBundle: FiltersBundleUI = FiltersBundleUI(),
        val filtersAmount: Int = 0,
        val sortType: SortTypeUI = SortTypeUI(),
        val isFirstLoadSorted: Boolean = false,
        val itemsList: List<Item> = emptyList(),
        val layoutManager: String = LINEAR,
        val showCategoryContainer: Boolean = false,
        val filterCode: String = "BRAND",
        val scrollToTop: Boolean = false,
    ) : State

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}