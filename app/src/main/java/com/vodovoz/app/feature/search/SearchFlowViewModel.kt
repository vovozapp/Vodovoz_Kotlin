package com.vodovoz.app.feature.search

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
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
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.data.parser.response.paginatedProducts.ProductsByQueryResponseJsonParser.parseProductsByQueryResponse
import com.vodovoz.app.data.parser.response.search.DefaultSearchDataResponseJsonParser.parseDefaultSearchDataResponse
import com.vodovoz.app.data.parser.response.search.MatchesQueriesResponseJsonParser.parseMatchesQueriesResponse
import com.vodovoz.app.data.parser.response.search.ProductsByQueryHeaderResponseJsonParser.parseProductsByQueryHeaderResponse
import com.vodovoz.app.feature.favorite.mapper.FavoritesMapper
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.DefaultSearchDataBundleMapper.mapToUI
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
class SearchFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager
) : PagingStateViewModel<SearchFlowViewModel.SearchState>(SearchState()){

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

    private val noMatchesToastListener = MutableSharedFlow<Boolean>()
    fun observeNoMatchesToast() = noMatchesToastListener.asSharedFlow()

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchDefaultSearchData()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchDefaultSearchData()
    }

    fun updateQuery(query: String) {
        uiStateListener.value = state.copy(data = state.data.copy(query = query, selectedCategoryId = -1, categoryHeader = null), loadingPage = true, page = 1, loadMore = false)
        fetchHeader()
        fetchProductsByQuery()
    }

    fun clearState() {
        uiStateListener.value = state.copy(data = state.data.copy(query = "", categoryHeader = null, itemsList = emptyList(), matchesQuery = emptyList()))
    }

    fun fetchDefaultSearchData() {
        uiStateListener.value = state.copy(data = state.data.copy(historyQuery = dataRepository.fetchSearchHistory()))

        viewModelScope.launch {
            flow { emit(repository.fetchSearchDefaultData()) }
                .catch {
                    debugLog { "fetch default search data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseDefaultSearchDataResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                popularCategoryDetail = data.popularProductsCategoryDetailUI,
                                popularQuery = data.popularQueryList
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

    private fun fetchHeader() {
        if (state.data.query.isNotEmpty()) {
            dataRepository.addQueryToHistory(state.data.query)
            uiStateListener.value = state.copy(data = state.data.copy(historyQuery = dataRepository.fetchSearchHistory()))
        }

        viewModelScope.launch {
            flow { emit(repository.fetchProductsByQueryHeader(query = state.data.query)) }
                .catch {
                    debugLog { "fetch products by query error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseProductsByQueryHeaderResponse()
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                categoryHeader = checkSelectedFilter(data)
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
                .collect()
        }
    }

    fun firstLoadSorted() {
        if (!state.data.isFirstLoadSorted) {
            uiStateListener.value = state.copy(data = state.data.copy(isFirstLoadSorted = true), loadingPage = true)
            fetchHeader()
            fetchProductsByQuery()
        }
    }

    fun refreshSorted() {
        uiStateListener.value = state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchHeader()
        fetchProductsByQuery()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem())
            fetchProductsByQuery()
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

    private fun fetchProductsByQuery() {

        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchProductsByQuery(
                        query = state.data.query,
                        categoryId = when(state.data.selectedCategoryId) {
                            -1L -> null
                            else -> state.data.selectedCategoryId
                        },
                        sort = state.data.sortType.value,
                        orientation = state.data.sortType.orientation,
                        page = state.page
                    )
                )
            }
                .catch {
                    debugLog { "fetch products by query sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseProductsByQueryResponse()
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

    fun fetchMatchesQueries(query: String) {
        if (query.isEmpty()) return
        viewModelScope.launch {
            flow { emit(repository.fetchMatchesQueries(query)) }
                .catch {
                    debugLog { "fetch matches query error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseMatchesQueriesResponse()
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                matchesQuery = response.data
                            ),
                            loadingPage = false,
                            error = null
                        )
                    } else {
                        uiStateListener.value =
                            state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .catch {
                    uiStateListener.value = state.copy(
                        data = state.data.copy(
                            matchesQuery = emptyList()
                        ),
                        loadingPage = false
                    )
                    noMatchesToastListener.emit(true)
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun clearSearchHistory() {
        dataRepository.clearSearchHistory()
        uiStateListener.value = state.copy(data = state.data.copy(historyQuery = emptyList()))
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

    fun changeRating(productId: Long, rating: Float, oldRating: Float) {
        viewModelScope.launch {
            ratingProductManager.rate(productId, rating = rating, oldRating = oldRating)
        }
    }

    fun onTabClick(id: Long) {
        val categoryUI = state.data.categoryHeader ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                categoryHeader = categoryUI.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == id) }
                ),
                selectedCategoryId = id,
                sortType = SortType.NO_SORT
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchProductsByQuery()
    }

    fun updateBySortType(sortType: SortType) {
        if (state.data.sortType == sortType) return
        val categoryUI = state.data.categoryHeader
        uiStateListener.value = state.copy(
            data = state.data.copy(
                sortType = sortType,
                selectedCategoryId = -1,
                categoryHeader = categoryUI?.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == -1L) }
                )
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchHeader()
    }

    data class SearchState(
        val query: String = "",
        val categoryHeader: CategoryUI? = null,
        val popularCategoryDetail: CategoryDetailUI? = null,
        val popularQuery: List<String> = emptyList(),
        val historyQuery: List<String> = emptyList(),
        val matchesQuery: List<String> = emptyList(),
        val sortType: SortType = SortType.NO_SORT,
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