package com.vodovoz.app.feature.productlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.config.FiltersConfig
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.data.parser.response.category.CategoryHeaderResponseJsonParser.parseCategoryHeaderResponse
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByCategoryResponseJsonParser.parseProductsByCategoryResponse
import com.vodovoz.app.feature.favorite.mapper.FavoritesMapper
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.*
import com.vodovoz.app.ui.model.custom.FiltersBundleUI
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterQuery
import com.vodovoz.app.util.FilterBuilderExtensions.buildFilterValueQuery
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsListFlowViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val catalogManager: CatalogManager,
    private val ratingProductManager: RatingProductManager
) : PagingStateViewModel<ProductsListFlowViewModel.ProductsListState>(ProductsListState()) {

    private var categoryId = savedState.get<Long>("categoryId") ?: 0

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

    private fun fetchCategoryHeader() {
        viewModelScope.launch {
            flow { emit(repository.fetchCategoryHeader(categoryId)) }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCategoryHeaderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                categoryHeader = data,
                                categoryId = categoryId,
                                showCategoryContainer = catalogManager.hasRootItems(categoryId),
                                filterCode = data.filterCode.ifEmpty {
                                    state.data.filterCode
                                }
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
                    debugLog { "fetch category header error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    private fun fetchProductsByCategory() {
        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchProductsByCategory(
                        categoryId = categoryId ?: return@flow,
                        sort = state.data.sortType.value,
                        orientation = state.data.sortType.orientation,
                        filter = state.data.filterBundle.filterUIList.buildFilterQuery(),
                        filterValue = state.data.filterBundle.filterUIList.buildFilterValueQuery(),
                        priceFrom = state.data.filterBundle.filterPriceUI.minPrice,
                        priceTo = state.data.filterBundle.filterPriceUI.maxPrice,
                        page = state.page
                    )
                )
            }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseProductsByCategoryResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        val mappedFeed = FavoritesMapper.mapFavoritesListByManager(
                            state.data.layoutManager,
                            data
                        )

                        uiStateListener.value = if (data.isEmpty() && !state.loadMore) {
                            state.copy(
                                error = ErrorState.Empty(),
                                data = state.data.copy(itemsList = emptyList()),
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
            fetchCategoryHeader()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchCategoryHeader()
    }

    fun firstLoadSorted() {
        if (!state.data.isFirstLoadSorted) {
            uiStateListener.value =
                state.copy(data = state.data.copy(isFirstLoadSorted = true), loadingPage = true)
            fetchProductsByCategory()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchCategoryHeader()
        fetchProductsByCategory()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem())
            fetchProductsByCategory()
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
        fetchCategoryHeader()
        fetchProductsByCategory()
    }

    fun updateBySortType(sortType: SortType) {
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
        fetchCategoryHeader()
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
                    primaryFilterValueList = categoryUI.primaryFilterValueList.map { it.copy(isSelected = it.id == filterValue.id) }
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
        val sortType: SortType = SortType.NO_SORT,
        val isFirstLoadSorted: Boolean = false,
        val itemsList: List<Item> = emptyList(),
        val layoutManager: String = LINEAR,
        val showCategoryContainer: Boolean = false,
        val filterCode: String = "BRAND"
    ) : State

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}