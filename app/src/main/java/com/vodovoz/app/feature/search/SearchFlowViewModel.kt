package com.vodovoz.app.feature.search

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.search.SearchManager
import com.vodovoz.app.core.network.ApiConfig
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SearchQueryHeaderResponse
import com.vodovoz.app.data.model.common.SearchQueryResponse
import com.vodovoz.app.feature.favorite.mapper.FavoritesMapper
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.DefaultSearchDataBundleMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.mapper.QuickSearchDataBundleMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.SortTypeUI
import com.vodovoz.app.ui.model.custom.QuickQueryBundleUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager,
    private val searchManager: SearchManager,
) : PagingContractViewModel<SearchFlowViewModel.SearchState, SearchFlowViewModel.SearchEvents>(
    SearchState()
) {

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

    private val noMatchesToastListener = MutableSharedFlow<Boolean>()
    fun observeNoMatchesToast() = noMatchesToastListener.asSharedFlow()

    private val changeQueryState = MutableStateFlow("")

    init {
        viewModelScope.launch {
            changeQueryState.debounce(500).distinctUntilChanged()
                .collect {
                    debugLog { "Search query: $it" }
                    if(it.isEmpty()){
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                matchesQuery = null,
                                mayBeSearchDetail = null
                            ))
                    }
                    fetchMatchesQueries(it)
                }
        }
    }

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
        uiStateListener.value = state.copy(
            data = state.data.copy(
                query = query,
                selectedCategoryId = -1,
                categoryHeader = null
            ), loadingPage = true, page = 1, loadMore = false
        )
        fetchHeader()
        fetchProductsByQuery()
    }

    fun clearState() {
        uiStateListener.value = state.copy(
            data = state.data.copy(
                query = "",
                categoryHeader = null,
                itemsList = emptyList(),
                matchesQuery = null,
                mayBeSearchDetail = null
            )
        )
    }

    fun fetchDefaultSearchData() {
        uiStateListener.value =
            state.copy(data = state.data.copy(historyQuery = searchManager.fetchSearchHistory()))

        viewModelScope.launch {
            flow { emit(repository.fetchSearchDefaultData()) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                popularCategoryDetail = data.popularProductsCategoryDetailUI.copy(
                                    productUIList = data.popularProductsCategoryDetailUI.productUIList.map {
                                        it.copy(linear = false)
                                    }
                                ),
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
                .catch {
                    debugLog { "fetch default search data error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    private fun fetchHeader() {
        if (state.data.query.isNotEmpty()) {
            searchManager.addQueryToHistory(state.data.query)
            uiStateListener.value =
                state.copy(data = state.data.copy(historyQuery = searchManager.fetchSearchHistory()))
        }

        viewModelScope.launch {
            flow { emit(repository.fetchProductsByQueryHeader(query = state.data.query)) }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val searchResponse: SearchQueryHeaderResponse = response.data
                        val deepLink = searchResponse.deepLink
                        if (deepLink.isNotEmpty()) {
                            uiStateListener.value = state.copy(
                                loadingPage = false,
                                error = null
                            )
                            when (deepLink) {
                                "dostavka" -> {
                                    eventListener.emit(
                                        SearchEvents.GoToWebView(
                                            url = ApiConfig.ABOUT_DELIVERY_URL,
                                            title = "О доставке"
                                        )
                                    )
                                }

                                "oplata" -> {
                                    eventListener.emit(
                                        SearchEvents.GoToWebView(
                                            url = ApiConfig.ABOUT_PAY_URL,
                                            title = "Об оплате"
                                        )
                                    )
                                }

                                in listOf("remontkyler", "arendakyler", "obrabotkakyler") -> {
                                    eventListener.emit(
                                        SearchEvents.GoToService(
                                            id = searchResponse.id
                                        )
                                    )
                                }

                                "akcii" -> {
                                    eventListener.emit(SearchEvents.GoToPromotions)
                                }

                                "kontakty" -> {
                                    eventListener.emit(SearchEvents.GoToContacts)
                                }

                                else -> {
                                    uiStateListener.value =
                                        state.copy(
                                            loadingPage = false,
                                            error = ErrorState.Error(),
                                            page = 1,
                                            loadMore = false
                                        )
                                }
                            }

                        } else {
                            val data = searchResponse.category.mapToUI()
                            uiStateListener.value = state.copy(
                                data = state.data.copy(
                                    categoryHeader = checkSelectedFilter(data),
                                    sortType = data.sortTypeList?.sortTypeList?.firstOrNull { it.value == "default" }
                                        ?: SortTypeUI(sortName = "По популярности")
                                ),
                                loadingPage = false,
                                error = null
                            )
                        }
                    } else {
                        uiStateListener.value =
                            state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "fetch products by query error ${it.localizedMessage}" }
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
            fetchHeader()
            fetchProductsByQuery()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchHeader()
        fetchProductsByQuery()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem(), data = state.data.copy(scrollToTop = false))
            fetchProductsByQuery(true)
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

    private fun fetchProductsByQuery(checkDeepLink: Boolean = false) {

        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchProductsByQuery(
                        query = state.data.query,
                        categoryId = when (state.data.selectedCategoryId) {
                            -1L -> null
                            else -> state.data.selectedCategoryId
                        },
                        sort = state.data.sortType.value,
                        orientation = state.data.sortType.orientation,
                        page = state.page
                    )
                )
            }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val searchResponse: SearchQueryResponse = response.data
                        val deepLink = searchResponse.deepLink
                        if (deepLink.isNotEmpty()) {
                            if (checkDeepLink) {
                                uiStateListener.value = state.copy(
                                    loadingPage = false,
                                    error = null
                                )
                                when (deepLink) {
                                    "dostavka" -> {
                                        eventListener.emit(
                                            SearchEvents.GoToWebView(
                                                url = ApiConfig.ABOUT_DELIVERY_URL,
                                                title = "О доставке"
                                            )
                                        )
                                    }

                                    "oplata" -> {
                                        eventListener.emit(
                                            SearchEvents.GoToWebView(
                                                url = ApiConfig.ABOUT_PAY_URL,
                                                title = "Об оплате"
                                            )
                                        )
                                    }

                                    in listOf("remontkyler", "arendakyler", "obrabotkakyler") -> {
                                        eventListener.emit(
                                            SearchEvents.GoToService(
                                                id = searchResponse.id
                                            )
                                        )
                                    }

                                    "akcii" -> {
                                        eventListener.emit(SearchEvents.GoToPromotions)
                                    }

                                    "kontakty" -> {
                                        eventListener.emit(SearchEvents.GoToContacts)
                                    }

                                    else -> {
                                        uiStateListener.value =
                                            state.copy(
                                                loadingPage = false,
                                                error = ErrorState.Error(),
                                                page = 1,
                                                loadMore = false
                                            )
                                    }
                                }
                            }

                        } else {
                            val data = searchResponse.productList.mapToUI()
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
                                    data = state.data.copy(itemsList = itemsList, scrollToTop = state.page == 1),
                                    error = null,
                                    loadMore = false,
                                    bottomItem = null
                                )
                            }
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
                    debugLog { "fetch products by query sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun clearScrollState() {
        uiStateListener.value = state.copy(data = state.data.copy(scrollToTop = false))
    }

    fun fetchMatchesQueries(query: String) {
        if (query.isEmpty()) {
            uiStateListener.value = state.copy(
                data = state.data.copy(
                    matchesQuery = null,
                    mayBeSearchDetail = null
                ),
                loadingPage = false,
                error = null
            )
            return
        }
        viewModelScope.launch {
            flow { emit(repository.fetchMatchesQueries(query)) }
                .catch {
                    debugLog { "fetch matches query error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                matchesQuery = data.quickQueryBundleUI,
                                mayBeSearchDetail = data.quickProductsCategoryDetailUI?.copy(
                                    productUIList = data.quickProductsCategoryDetailUI.productUIList.map { it.copy(
                                            linear = false
                                        )
                                    }
                                )
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
                            matchesQuery = null,
                            mayBeSearchDetail = null
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
        searchManager.clearSearchHistory()
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

    fun changeQuery(query: String) {
        changeQueryState.value = query
    }

    fun onTabClick(id: Long) {
        val categoryUI = state.data.categoryHeader ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                categoryHeader = categoryUI.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == id) }
                ),
                selectedCategoryId = id,
                sortType = SortTypeUI()
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchProductsByQuery(true)
    }

    fun updateBySortType(sortType: SortTypeUI) {
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
        fetchProductsByQuery()
    }

    fun onPreOrderClick(id: Long, name: String, detailPicture: String) {
        viewModelScope.launch {
            val accountId = accountManager.fetchAccountId()
            if (accountId == null) {
                //  eventListener.emit(SearchEvents.GoToProfile)
                eventListener.emit(SearchEvents.GoToPreOrder(id, name, detailPicture))
            } else {
                eventListener.emit(SearchEvents.GoToPreOrder(id, name, detailPicture))
            }
        }
    }

    sealed class SearchEvents : Event {
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) :
            SearchEvents()

        data object GoToProfile : SearchEvents()

        data class GoToWebView(val url: String, val title: String) : SearchEvents()

        data class GoToService(val id: String) : SearchEvents()

        data object GoToContacts : SearchEvents()

        data object GoToPromotions : SearchEvents()
    }

    data class SearchState(
        val query: String = "",
        val categoryHeader: CategoryUI? = null,
        val popularCategoryDetail: CategoryDetailUI? = null,
        val mayBeSearchDetail: CategoryDetailUI? = null,
        val popularQuery: List<String> = emptyList(),
        val historyQuery: List<String> = emptyList(),
        val matchesQuery: QuickQueryBundleUI? = null,
        val sortType: SortTypeUI = SortTypeUI(),
        val selectedCategoryId: Long = -1,
        val isFirstLoadSorted: Boolean = false,
        val itemsList: List<Item> = emptyList(),
        val layoutManager: String = LINEAR,
        val scrollToTop: Boolean = false,
    ) : State

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}