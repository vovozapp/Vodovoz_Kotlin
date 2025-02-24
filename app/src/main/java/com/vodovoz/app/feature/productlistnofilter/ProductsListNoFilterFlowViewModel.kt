package com.vodovoz.app.feature.productlistnofilter

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsSectionModel
import com.vodovoz.app.domain.general.model.ProductsSectionUi
import com.vodovoz.app.domain.general.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.favorite.mapper.FavoritesMapper
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.toUi
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toDomain
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment.DataSource
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.SortTypeUI
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsListNoFilterFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingStateViewModel<ProductsListNoFilterFlowViewModel.ProductListNoFilterState>(
    ProductListNoFilterState()
) {

    private val dataSource = savedState.get<DataSource>("dataSource") ?: DataSource.Missing

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

    private fun fetchProductListData() = viewModelScope.launch {
        val categoryId = dataState.currentCategory.id
        val sortModel = dataState.currentSort.toDomain()

        when (dataSource) {
            is DataSource.Brand -> {
                TODO()
            }

            is DataSource.ButtonProducts -> {
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getAllSuperTop(dataSource.buttonId).single()
                    },
                    fetchPagedProductsFlow = {
                        vodovozServiceRepository.getAllSuperTopPaged(
                            id = dataSource.buttonId,
                            categoryId = categoryId,
                            sort = sortModel
                        )
                    }
                )
            }

            is DataSource.Country -> {
                TODO()

            }

            is DataSource.Slider -> {
                TODO()
            }

            DataSource.HurryBuyUpProducts -> {
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getAllHurryUpBuyProducts().single()
                    },
                    fetchPagedProductsFlow = {
                        vodovozServiceRepository.getAllHurryUpBuyProductsPaged(
                            categoryId = categoryId,
                            sort = sortModel
                        )
                    }
                )
            }

            DataSource.Missing -> {
                TODO()
            }

            DataSource.NewProducts -> {
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getAllNewProducts().single()
                    },
                    fetchPagedProductsFlow = {
                        vodovozServiceRepository.getAllNewProductsPaged(
                            categoryId = categoryId,
                            sort = sortModel
                        )
                    }
                )
            }

            DataSource.ViewedProducts -> {
                TODO()
            }
        }

    }

    fun showSortBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                showSortBottomSheet = true
            )
        }
    }

    fun hideSortBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                showSortBottomSheet = false
            )
        }
    }


    private suspend fun fetchProductsData(
        fetchProductsSection: suspend () -> Result<ProductsSectionModel>,
        fetchPagedProductsFlow: () -> Flow<PagingData<ProductModel>>,
    ) {
        val productsSectionResult = if (dataState.productsSection == ProductsSectionUi.Empty) {
            fetchProductsSection().map { it.toUi() }
        } else {
            Result.success(dataState.productsSection)
        }

        val pagedProductsFlow = fetchPagedProductsFlow().map { pagingData ->
            pagingData.map { productModel -> productModel.toUi() }
        }

        productsSectionResult.onSuccess { productsSection ->
            uiStateListener.updateData { state ->
                state.copy(
                    productsSection = productsSection,
                    pagedProducts = pagedProductsFlow,
                    uiState = UiState.Success,
                    currentSort = productsSection.sorting.firstOrNull { it.name == productsSection.sortingTitle }
                        ?: productsSection.sorting.firstOrNull() ?: SortUi.Empty,
                )
            }
        }.onFailure {
            uiStateListener.updateData { state ->
                state.copy(uiState = UiState.Error)
            }
        }
    }

    private fun fetchHeaderByDataSource() {
        viewModelScope.launch {
            val dataSource = dataSource
            flow {
                when (dataSource) {
                    is DataSource.Brand -> emit(repository.fetchBrandHeader(dataSource.brandId))
                    is DataSource.Country -> emit(repository.fetchCountryHeader(dataSource.countryId))
                    is DataSource.HurryBuyUpProducts -> emit(repository.fetchDiscountHeader())
                    is DataSource.NewProducts -> emit(repository.fetchNoveltiesHeader())
                    is DataSource.Slider -> emit(repository.fetchDoubleSliderHeader(dataSource.categoryId))
                    is DataSource.ButtonProducts -> {

                    }

                    DataSource.ViewedProducts -> {

                    }

                    DataSource.Missing -> {

                    }
                }
            }
                .onEach { response ->
                    if (response is ResponseEntity.Success) {
                        val data = response.data.mapToUI()
                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                categoryHeader = checkSelectedFilter(data),
                                categoryId = data.id ?: -1,
                                sortType = data.sortTypeList?.sortTypeList?.firstOrNull {
                                    it.value == state.data.sortType.value && it.orientation == state.data.sortType.orientation
                                } ?: SortTypeUI(sortName = "По популярности", value = "default")
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
                    debugLog { "fetch header error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    private fun fetchProductsByDataSource() {
        viewModelScope.launch {
            val dataSource = dataSource ?: return@launch
            flow {
                when (dataSource) {
                    is DataSource.Brand -> emit(
                        repository.fetchProductsByBrand(
                            brandId = dataSource.brandId,
                            code = null,
                            categoryId = when (state.data.selectedCategoryId) {
                                -1L -> null
                                else -> state.data.selectedCategoryId
                            },
                            sort = state.data.sortType.value,
                            orientation = state.data.sortType.orientation,
                            page = state.page
                        )
                    )

                    is DataSource.Country -> emit(
                        repository.fetchProductsByCountry(
                            countryId = dataSource.countryId,
                            categoryId = when (state.data.selectedCategoryId) {
                                -1L -> null
                                else -> state.data.selectedCategoryId
                            },
                            sort = state.data.sortType.value,
                            orientation = state.data.sortType.orientation,
                            page = state.page
                        )
                    )

                    is DataSource.HurryBuyUpProducts -> emit(
                        repository.fetchProductsByDiscount(
                            categoryId = when (state.data.selectedCategoryId) {
                                -1L -> null
                                else -> state.data.selectedCategoryId
                            },
                            sort = state.data.sortType.value,
                            orientation = state.data.sortType.orientation,
                            page = state.page
                        )
                    )

                    is DataSource.NewProducts -> emit(
                        repository.fetchProductsByNovelties(
                            categoryId = when (state.data.selectedCategoryId) {
                                -1L -> null
                                else -> state.data.selectedCategoryId
                            },
                            sort = state.data.sortType.value,
                            orientation = state.data.sortType.orientation,
                            page = state.page
                        )
                    )

                    is DataSource.Slider -> {
                        emit(
                            repository.fetchProductsByDoubleSlider(
                                categoryId = dataSource.categoryId,
                                sectionId = when (state.data.selectedCategoryId) {
                                    -1L -> null
                                    else -> state.data.selectedCategoryId
                                },
                                sort = state.data.sortType.value,
                                orientation = state.data.sortType.orientation,
                                page = state.page
                            )
                        )
                    }

                    is DataSource.ButtonProducts -> {

                    }

                    DataSource.ViewedProducts -> {

                    }

                    DataSource.Missing -> TODO()
                }
            }
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
                                data = state.data.copy(
                                    itemsList = itemsList,
                                    scrollToTop = state.page == 1
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
                    debugLog { "fetch products by data source sorted error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun clearScrollState() {
        uiStateListener.value = state.copy(data = state.data.copy(scrollToTop = false))
    }

    fun firstLoad() {
        fetchProductListData()
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchHeaderByDataSource()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchHeaderByDataSource()
    }

    fun firstLoadSorted() {
        if (!state.data.isFirstLoadSorted) {
            uiStateListener.value =
                state.copy(data = state.data.copy(isFirstLoadSorted = true), loadingPage = true)
            fetchProductsByDataSource()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
        fetchHeaderByDataSource()
        fetchProductsByDataSource()
    }

    fun loadMoreSorted() {
        if (state.bottomItem == null && state.page != null) {
            uiStateListener.value = state.copy(
                loadMore = true,
                bottomItem = BottomProgressItem(),
                data = state.data.copy(scrollToTop = false)
            )
            fetchProductsByDataSource()
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
        val categoryUI = state.data.categoryHeader ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                categoryHeader = categoryUI.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == categoryId) }
                ),
                selectedCategoryId = categoryId,
                sortType = SortTypeUI()
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchProductsByDataSource()
    }

    fun onTabClick(id: Long) {
        val categoryUI = state.data.categoryHeader ?: return

        uiStateListener.value = state.copy(
            data = state.data.copy(
                categoryHeader = categoryUI.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == id) }
                ),
                selectedCategoryId = id,
            ),
            page = 1,
            loadMore = false
        )
        fetchProductsByDataSource()
    }

    fun updateBySortType(sortType: SortTypeUI) {
        if (state.data.sortType == sortType) return
        val categoryUI = state.data.categoryHeader
        uiStateListener.value = state.copy(
            data = state.data.copy(
                sortType = sortType,
                categoryHeader = categoryUI?.copy(
                    categoryUIList = categoryUI.categoryUIList.map { it.copy(isSelected = it.id == -1L) }
                ),
                scrollToTop = true,
            ),
            page = 1,
            loadMore = false,
            loadingPage = true
        )
        fetchProductsByDataSource()
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

    fun selectSort(sort: SortUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentSort = sort,
                showSortBottomSheet = false
            )
        }
        fetchProductListData()
    }

    fun switchLayout() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                isGridView = !s.isGridView
            )
        }
    }

    fun selectCategory(category: CategoryUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentCategory = category
            )
        }
        fetchProductListData()
    }

    @Immutable
    data class ProductListNoFilterState(
        val categoryId: Long = -1,
        val categoryHeader: CategoryUI? = null,
        val sortType: SortTypeUI = SortTypeUI(),
        val isFirstLoadSorted: Boolean = false,
        val itemsList: List<Item> = emptyList(),
        val layoutManager: String = LINEAR,
        val selectedCategoryId: Long = -1,
        val scrollToTop: Boolean = false,

        val productsSection: ProductsSectionUi = ProductsSectionUi.Empty,
        val pagedProducts: Flow<PagingData<ProductUi>> = emptyFlow(),
        val currentCategory: CategoryUi = CategoryUi.Empty,
        val currentSort: SortUi = SortUi.Empty,
        val uiState: UiState = UiState.Loading,
        val isGridView: Boolean = true,
        val showSortBottomSheet: Boolean = false,
    ) : State

    sealed interface UiState {
        data object Error : UiState
        data object Loading : UiState
        data object Success : UiState
    }

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}