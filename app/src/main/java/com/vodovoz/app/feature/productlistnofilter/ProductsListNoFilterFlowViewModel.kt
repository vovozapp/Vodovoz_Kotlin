package com.vodovoz.app.feature.productlistnofilter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.common.content.itemadapter.bottomitem.BottomProgressItem
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.feature.favorite.mapper.FavoritesMapper
import com.vodovoz.app.feature.productlistnofilter.PaginatedProductsCatalogWithoutFiltersFragment.DataSource
import com.vodovoz.app.mapper.CategoryMapper.mapToUI
import com.vodovoz.app.mapper.ProductMapper.mapToUI
import com.vodovoz.app.ui.model.CategoryUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.SortTypeUI
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
class ProductsListNoFilterFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
) : PagingStateViewModel<ProductsListNoFilterFlowViewModel.ProductListNoFilterState>(
    ProductListNoFilterState()
) {

    private var dataSource = savedState.get<DataSource>("dataSource")

    private val changeLayoutManager = MutableStateFlow(LINEAR)
    fun observeChangeLayoutManager() = changeLayoutManager.asStateFlow()

    private fun fetchHeaderByDataSource() {
        viewModelScope.launch {
            val dataSource = dataSource ?: return@launch
            flow {
                when (dataSource) {
                    is DataSource.Brand -> emit(repository.fetchBrandHeader(dataSource.brandId))
                    is DataSource.Country -> emit(repository.fetchCountryHeader(dataSource.countryId))
                    is DataSource.Discount -> emit(repository.fetchDiscountHeader())
                    is DataSource.Novelties -> emit(repository.fetchNoveltiesHeader())
                    is DataSource.Slider -> emit(repository.fetchDoubleSliderHeader(dataSource.categoryId))
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

                    is DataSource.Discount -> emit(
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

                    is DataSource.Novelties -> emit(
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
            uiStateListener.value = state.copy(loadMore = true, bottomItem = BottomProgressItem(), data = state.data.copy(scrollToTop = false))
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

    data class ProductListNoFilterState(
        val categoryId: Long = -1,
        val categoryHeader: CategoryUI? = null,
        val sortType: SortTypeUI = SortTypeUI(),
        val isFirstLoadSorted: Boolean = false,
        val itemsList: List<Item> = emptyList(),
        val layoutManager: String = LINEAR,
        val selectedCategoryId: Long = -1,
        val scrollToTop: Boolean = false,
    ) : State

    companion object {
        const val LINEAR = "linear"
        const val GRID = "grid"
    }
}