package com.vodovoz.app.feature.productlistnofilter

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import com.vodovoz.app.R
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.common.resources.ResourcesProvider
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.design_system.model.ParentCategoryUi
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.allCategories
import com.vodovoz.app.design_system.model.filters.FiltersPriceUi
import com.vodovoz.app.design_system.model.filters.FiltersUi
import com.vodovoz.app.design_system.model.filters.toDomain
import com.vodovoz.app.design_system.model.toCategory
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.design_system.model.withUpdatedCart
import com.vodovoz.app.design_system.model.withUpdatedFavorites
import com.vodovoz.app.design_system.model.withUpdatedLoading
import com.vodovoz.app.domain.general.model.EmptyResultException
import com.vodovoz.app.domain.general.model.FiltersModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.model.ProductsSectionModel
import com.vodovoz.app.domain.general.model.ProductsSectionUi
import com.vodovoz.app.domain.general.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.feature.home.model.toParentCategory
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toDomain
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment.DataSource
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.SortTypeUI
import com.vodovoz.app.ui.paging.PagingDataListener
import com.vodovoz.app.ui.paging.copy
import com.vodovoz.app.ui.paging.emptyCombinedLoadStates
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
    private val resourcesProvider: ResourcesProvider,
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
        configureScreen().invokeOnCompletion {
            fetchProductListData()
        }
    }


    private fun configureScreen() = viewModelScope.launch {
        if (dataSource is DataSource.Category) {
            uiStateListener.updateData { s ->
                val currentCategory = CategoryUi(id = dataSource.categoryId.toInt(), name = "")
                s.copy(
                    currentCategory = currentCategory,
                    showFilters = true,
                    currentBottomSheetCategory = currentCategory.toParentCategory()
                )
            }
        } else {
            uiStateListener.updateData { s ->
                s.copy(
                    showEmptyCategory = true,
                    showFilters = false
                )
            }
        }
    }


    private fun fetchProductListData() = viewModelScope.launch {
        if (dataState.productsSection == ProductsSectionUi.Empty) {
            uiStateListener.updateData { s ->
                s.copy(uiState = UiState.Loading)
            }
        }

        val categoryId = dataState.currentCategory.id
        val sortModel = dataState.currentSort.toDomain()

        when (dataSource) {
            is DataSource.Brand -> {
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getBrandProducts(
                            dataSource.brandId,
                            sortModel,
                            categoryId
                        ).singleResult()
                    },
                    fetchPagedProductsFlow = {
                        vodovozServiceRepository.getBrandProductsPaged(
                            dataSource.brandId,
                            sortModel,
                            categoryId
                        )
                    }
                )
            }

            is DataSource.ButtonProducts -> {
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getAllSuperTop(dataSource.buttonId).singleResult()
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

            DataSource.HurryBuyUpProducts -> {
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getAllHurryUpBuyProducts().singleResult()
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
                        vodovozServiceRepository.getAllNewProducts().singleResult()
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
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getBannerProducts(
                            dataSource.bannerId,
                            dataSource.blockId,
                            sortModel,
                            categoryId
                        ).singleResult()
                    },
                    fetchPagedProductsFlow = {
                        vodovozServiceRepository.getBannerProductsPaged(
                            dataSource.bannerId,
                            dataSource.blockId,
                            sortModel,
                            categoryId
                        )
                    }
                )
            }

            is DataSource.Search -> {
                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getSearchProducts(dataSource.query).singleResult()
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
                val currentFilters = extractSelectedFilters()

                fetchProductsData(
                    fetchProductsSection = {
                        vodovozServiceRepository.getCategoryProducts(
                            categoryId.toLong(),
                            currentFilters
                        ).singleResult()
                    },
                    fetchPagedProductsFlow = {
                        vodovozServiceRepository.getCategoryProductsPaged(
                            categoryId.toLong(),
                            sortModel,
                            currentFilters
                        )
                    }
                )
            }


            DataSource.Missing -> {
                navigateBack()
            }

        }

    }

    private fun extractSelectedFilters(): FiltersModel {
        val dataStateFilters = dataState.currentFilters

        val filteredFilters = dataStateFilters.filters.filter { filterUi ->
            filterUi.values.any { filterValueUi -> filterValueUi.selected }
        }.map { filterUi ->
            filterUi.copy(values = filterUi.values.filter { value -> value.selected })
        }

        val price = if (dataStateFilters == FiltersUi.Empty) {
            FiltersPriceUi(0, Int.MAX_VALUE)
        } else {
            dataStateFilters.price
        }

        return dataStateFilters.copy(
            filters = filteredFilters,
            price = price
        ).toDomain()
    }


    fun refresh() = viewModelScope.launch {
        if (dataState.uiState is UiState.Loading) return@launch
        uiStateListener.updateData { s ->
            s.copy(showRefreshIndicator = true)
        }
        fetchProductListData().join()

        uiStateListener.updateData { s ->
            s.copy(showRefreshIndicator = false)
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


        val categoriesTreeJob = fetchCategoriesTree(dataState.currentCategory.id.toLong())
        val productsSectionResult =
            fetchProductsSection().map { productsSectionModel ->
                productsSectionModel.toUi()
            }


        productsSectionResult.onSuccess { productsSection ->

            categoriesTreeJob.join()

            uiStateListener.updateData { state ->

                val categories = productsSection.categories.ifEmpty {
                    buildList {
                        addAll(dataState.categoryTree.allCategories()
                            .map { category -> category.toCategory() }
                        )
                        removeIf { it.id == state.currentCategory.id }
                    }
                }

                state.copy(
                    productsSection = productsSection.copy(categories = categories),
                    uiState = UiState.Body,
                    currentSort = state.currentSort.takeIf { sort ->
                        sort != SortUi.Empty
                    } ?: productsSection.sorting.firstOrNull() ?: SortUi.Empty,
                    currentBottomSheetCategory = state.currentCategory.toParentCategory(),
                    categoryTree = if (dataSource !is DataSource.Category) productsSection.categories.map { categoryUi ->
                        categoryUi.toParentCategory()
                    } else state.categoryTree
                )
            }

            viewModelScope.launch {
                fetchPagedProductsFlow().map { pagingData ->
                    pagingData.map { productModel -> productModel.toUi() }
                }.collect { pagingData ->
                    pagingProductsListener.collectPagingData(pagingData)
                }
            }

        }.onFailure { t ->
            val uiState = when (t) {
                is EmptyResultException -> with(t.errorData) {
                    UiState.Empty(
                        image = this?.imageUrl ?: "",
                        title = this?.headerHtml ?: resourcesProvider.getString(
                            R.string.empty_products_title
                        ),
                        description = this?.descriptionHtml
                            ?: resourcesProvider.getString(R.string.empty_products_description)
                    )
                }

                else -> UiState.Error
            }


            if (uiState is UiState.Empty && dataState.productsSection == ProductsSectionUi.Empty) {
                uiStateListener.updateData { s ->
                    s.copy(uiState = uiState)
                }
            } else if (dataState.productsSection != ProductsSectionUi.Empty) {
                uiStateListener.updateData { s ->
                    s.copy(
                        productsLoadStates = s.productsLoadStates.copy(
                            refresh = LoadState.Error(t)
                        ),
                        uiState = UiState.Body,
                        productsSection = s.productsSection.copy(
                            productsQuantityText = ""
                        )
                    )
                }
            } else {
                uiStateListener.updateData { s ->
                    s.copy(uiState = uiState)
                }
            }
        }
    }

    suspend fun listProductLoadings() = uiStateListener.map { state -> state.data.products }
        .combine(cartManager.blockedProductsState) { _, blockedProducts ->
            blockedProducts
        }.collectLatest { blockedProducts ->
            uiStateListener.updateData { s ->
                s.copy(
                    products = s.products.withUpdatedLoading(blockedProducts)
                )
            }
        }

    suspend fun listenCart() =
        uiStateListener.map { state ->
            state.data.products
        }.combine(cartManager.observeCarts()) { _, cart ->
            cart
        }.collectLatest { cart ->
            uiStateListener.updateData { s ->
                s.copy(
                    products = s.products.withUpdatedCart(cart)
                )
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

    private fun fetchCategoriesTree(categoryId: Long) = viewModelScope.launch {
        if (dataState.currentBottomSheetCategory.takeIf { it.id == categoryId && it.countChildren == 0 } != null) return@launch
        if (
            dataState.categoryTree.allCategories().firstOrNull {
                it.id == categoryId && it.countChildren == 0
            } != null
        ) return@launch
        if (dataSource !is DataSource.Category) return@launch

        val categoryTreeResult =
            vodovozServiceRepository.getCategoryTree(categoryId).singleResult()

        categoryTreeResult.onSuccess { categoryTree ->
            uiStateListener.updateData { s ->

                val bottomSheetCategories = categoryTree.map { category ->
                    category.toUi()
                }
                s.copy(categoryTree = bottomSheetCategories)
            }
        }.onFailure {
            if (dataState.categoryTree.isEmpty()) {
                uiStateListener.updateData { s ->
                    s.copy(showCategoriesBottomSheet = false)
                }
            }
        }
    }

    fun selectBottomSheetCategory(category: ParentCategoryUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(currentBottomSheetCategory = category)
        }

        fetchCategoriesTree(categoryId = category.id)
    }

    fun selectCategory(category: CategoryUi) = viewModelScope.launch {
        val newCategory =
            if (category == dataState.currentCategory && dataSource !is DataSource.Category) CategoryUi.Empty
            else if (category == dataState.currentCategory) return@launch
            else category

        uiStateListener.updateData { s ->
            s.copy(
                currentCategory = newCategory,
                productsLoadStates = s.productsLoadStates.copy(refresh = LoadState.Loading),
                products = emptyList(),
                currentFilters = FiltersUi.Empty,
                showCategoriesBottomSheet = false,
                currentBottomSheetCategory = newCategory.toParentCategory()
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

    fun navigateToProductDetails(product: ProductUi) = viewModelScope.launch {
        eventListener.emit(ProductListNoFilterEvent.GoToProductDetails(product.id))
    }

    fun changeFavorite(product: ProductUi) = viewModelScope.launch {
        likeManager.changeFavorite(product.id, !product.isFavorite)
    }

    fun notifyPagingProducts(index: Int) = viewModelScope.launch {
        kotlin.runCatching {
            pagingProductsListener[index]
        }
    }

    fun navigateToProductFilters() = viewModelScope.launch {
        with(dataState) {
            eventListener.emit(
                ProductListNoFilterEvent.GoToProductFilters(
                    currentCategory.id.toLong(),
                    currentFilters
                )
            )
        }
    }

    fun changeFilters(filters: FiltersUi) = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                currentFilters = filters,
                productsLoadStates = s.productsLoadStates.copy(refresh = LoadState.Loading),
                products = emptyList()
            )
        }
        eventListener.emit(ProductListNoFilterEvent.ScrollToTop)
        fetchProductListData()
    }

    fun shareProducts() = viewModelScope.launch {
        eventListener.emit(ProductListNoFilterEvent.Share(dataState.productsSection.share.text))
    }

    fun showCategoriesBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                showCategoriesBottomSheet = true,
                currentBottomSheetCategory = s.currentCategory.toParentCategory()
            )
        }
    }

    fun hideCategoriesBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(
                showCategoriesBottomSheet = false
            )
        }
    }

    fun chooseBottomSheetCategory() = viewModelScope.launch {
        val currentCategory = dataState.currentBottomSheetCategory.toCategory()
        selectCategory(currentCategory)
    }

    fun decrementProductToCart(product: ProductUi) = viewModelScope.launch {
        cartManager.change(product.id, product.cartQuantity - 1)
    }

    fun incrementProductToCart(product: ProductUi) = viewModelScope.launch {
        cartManager.change(product.id, product.cartQuantity + 1)
    }

    fun navigateToProductAnalogs(product: ProductUi) = viewModelScope.launch {
        eventListener.emit(ProductListNoFilterEvent.GoToProductAnalogs(product.id))
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
        val categoryTree: List<ParentCategoryUi> = emptyList(),

        val currentCategory: CategoryUi = CategoryUi.Empty,
        val currentBottomSheetCategory: ParentCategoryUi = ParentCategoryUi.Empty,
        val currentFilters: FiltersUi = FiltersUi.Empty,
        val currentSort: SortUi = SortUi.Empty,

        val uiState: UiState = UiState.Loading,

        val isGridView: Boolean = true,
        val showFilters: Boolean = false,
        val showCategoryList: Boolean = false,
        val showCategoriesBottomSheet: Boolean = false,
        val showSortBottomSheet: Boolean = false,
        val showRefreshIndicator: Boolean = false,
        val showEmptyCategory: Boolean = false,
    ) : State

    sealed interface UiState {
        data class Empty(val image: String, val title: String, val description: String) : UiState
        data object Error : UiState
        data object Loading : UiState
        data object Body : UiState
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
        data class GoToProductFilters(val categoryId: Long, val filters: FiltersUi) :
            ProductListNoFilterEvent()

        data class Share(val text: String) : ProductListNoFilterEvent()
        data class GoToProductAnalogs(val productId: Long) : ProductListNoFilterEvent()
    }

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}