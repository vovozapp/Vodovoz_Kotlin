package com.vodovoz.app.feature.productlistnofilter

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.design_system.model.withUpdatedFavorites
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsSectionModel
import com.vodovoz.app.domain.general.model.ProductsSectionUi
import com.vodovoz.app.domain.general.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toDomain
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment.DataSource
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.SortTypeUI
import com.vodovoz.app.ui.paging.PagingDataListener
import com.vodovoz.app.ui.paging.copy
import com.vodovoz.app.ui.paging.emptyCombinedLoadStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
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
) : PagingContractViewModel<ProductsListNoFilterFlowViewModel.ProductListNoFilterState, ProductsListNoFilterFlowViewModel.ProductListNoFilterEvent>(
    ProductListNoFilterState()
) {

    val dataSource = savedState.get<DataSource>("dataSource") ?: DataSource.Missing

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

    private val pagingProductsListener = PagingDataListener(
        onUpdateItems = { itemSnapshotList ->
            uiStateListener.updateData { s ->
                val pagedProducts = itemSnapshotList.mapNotNull { product -> product }
                s.copy(products = pagedProducts)
            }
        }
    )

    init {
        listenFavorites()
        listenProductsLoadStates()
    }

    fun fetchProductListData() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(uiState = UiState.Loading)
        }

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

            is DataSource.Products -> {
                TODO()

            }

            is DataSource.Search -> {
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getSearchProducts(dataSource.query).single()
                    },
                    fetchPagedProductsFlow = {
                        vodovozServiceRepository.getSearchProductsPaged(
                            dataSource.query,
                            categoryId,
                            sortModel
                        )
                    }
                )
            }

            is DataSource.Category -> {
                TODO()
            }


            DataSource.Missing -> {
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



        productsSectionResult.onSuccess { productsSection ->


            uiStateListener.updateData { state ->
                state.copy(
                    productsSection = productsSection,
                    uiState = UiState.Success,
                    currentSort = state.currentSort.takeIf { sort ->
                        sort != SortUi.Empty
                    } ?: productsSection.sorting.firstOrNull() ?: SortUi.Empty,
                )
            }

            fetchPagedProductsFlow().map { pagingData ->
                pagingData.map { productModel -> productModel.toUi() }
            }.collect { pagingData -> pagingProductsListener.collectPagingData(pagingData) }

        }.onFailure {
            uiStateListener.updateData { state ->
                state.copy(uiState = UiState.Error)
            }
        }
    }


    private fun listenFavorites() = viewModelScope.launch {
        uiStateListener.map { pagingState -> pagingState.data.products }
            .combine(likeManager.observeLikes()) { products, favorites ->
                products to favorites
            }.collectLatest { (products, favorites) ->
                uiStateListener.updateData { s ->
                    s.copy(
                        products = products.withUpdatedFavorites(favorites)
                    )
                }
            }
    }

    private fun listenProductsLoadStates() = viewModelScope.launch {
        pagingProductsListener.collectLoadState { combinedLoadStates ->
            val refreshState = when {
                combinedLoadStates.refresh is LoadState.Loading && dataState.products.isNotEmpty() -> {
                    dataState.productsLoadStates.refresh
                }

                else -> combinedLoadStates.refresh
            }

            uiStateListener.updateData { s ->
                s.copy(
                    productsLoadStates = combinedLoadStates.copy(
                        refresh = refreshState
                    )
                )
            }
        }

    }


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

    fun selectSort(sort: SortUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentSort = sort,
                showSortBottomSheet = false,
                products = emptyList()
            )
        }
        eventListener.emit(ProductListNoFilterEvent.ScrollToTop)
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
        val newCategory = if (category == dataState.currentCategory) CategoryUi.Empty else category
        uiStateListener.updateData { s ->
            s.copy(
                currentCategory = newCategory,
                productsLoadStates = s.productsLoadStates.copy(refresh = LoadState.Loading),
                products = emptyList()
            )
        }
        eventListener.emit(ProductListNoFilterEvent.ScrollToTop)
        fetchProductListData()
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(ProductListNoFilterEvent.GoBack)
    }

    fun navigateToSearch(query: String) = viewModelScope.launch {
        eventListener.emit(ProductListNoFilterEvent.GoToSearch(query))
    }

    fun navigateToCategories() = viewModelScope.launch {
        eventListener.emit(
            ProductListNoFilterEvent.GoToCategories(
                dataState.productsSection.categories,
                dataState.currentCategory
            )
        )
    }

    fun navigateToProductDetails(product: ProductUi) = viewModelScope.launch {
        eventListener.emit(ProductListNoFilterEvent.GoToProductDetails(product.id))
    }

    fun changeFavorite(product: ProductUi) = viewModelScope.launch {
        likeManager.changeFavorite(product.id, !product.isFavorite)
    }

    fun notifyPagingProducts(index: Int) = kotlin.runCatching {
        pagingProductsListener[index]
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
        val products: List<ProductUi> = emptyList(),
        val productsLoadStates: CombinedLoadStates = emptyCombinedLoadStates,
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

    sealed class ProductListNoFilterEvent : Event {
        data object GoBack : ProductListNoFilterEvent()
        data object ScrollToTop : ProductListNoFilterEvent()

        data class GoToSearch(val query: String) : ProductListNoFilterEvent()
        data class GoToCategories(
            val categories: List<CategoryUi>,
            val currentCategory: CategoryUi,
        ) : ProductListNoFilterEvent()

        data class GoToProductDetails(val productId: Long) : ProductListNoFilterEvent()
    }

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}