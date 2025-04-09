package com.vodovoz.app.feature.products_collection

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.withUpdatedCart
import com.vodovoz.app.design_system.model.withUpdatedFavorites
import com.vodovoz.app.design_system.model.withUpdatedLoading
import com.vodovoz.app.domain.general.model.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.product_comments.model.SortUi
import com.vodovoz.app.feature.product_comments.model.toDomain
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionEvent
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionState
import com.vodovoz.app.feature.products_collection.model.ProductsCollectionUiState
import com.vodovoz.app.ui.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsCollectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val cartManager: CartManager,
    private val favoritesManager: LikeManager,
) : MviViewModel<ProductsCollectionState, ProductsCollectionEvent>(
    ProductsCollectionState()
) {
    private val productId = savedStateHandle.get<Long>("productId") ?: -1

    suspend fun listenProductLoadings() =
        state.map { it.productsSection.products }
            .distinctUntilChanged()
            .combine(cartManager.blockedProductsState) { _, blockedProducts ->
                blockedProducts
            }.collectLatest { blockedProducts ->
                _state.update { s ->
                    val productsSection = s.productsSection
                    s.copy(
                        productsSection = productsSection.copy(
                            products = productsSection.products.withUpdatedLoading(blockedProducts)
                        ),
                    )
                }
            }

    suspend fun listenFavorites() = state.map { it.productsSection.products }
        .distinctUntilChanged()
        .combine(favoritesManager.observeLikes()) { _, favorites ->
            favorites
        }.collectLatest { favorites ->
            _state.update { s ->
                val productsSection = s.productsSection
                s.copy(
                    productsSection = productsSection.copy(
                        products = productsSection.products.withUpdatedFavorites(favorites)
                    ),
                )
            }
        }

    suspend fun listenCart() = state.map { it.productsSection.products }
        .distinctUntilChanged()
        .combine(cartManager.observeCarts()) { _, cart ->
            cart
        }.collectLatest { cart ->
            _state.update { s ->
                val productsSection = s.productsSection
                s.copy(
                    productsSection = productsSection.copy(
                        products = productsSection.products.withUpdatedCart(cart)
                    ),
                )
            }
        }

    fun fetchProducts() =
        vodovozServiceRepository.getProductAnalogs(productId, stateSnapshot.currentSort.toDomain())
            .onStart {
                _state.update { s -> s.copy(uiState = ProductsCollectionUiState.Loading) }
            }.onEach { result ->
                result.onSuccess { productsSectionModel ->
                    val productsSectionUi = productsSectionModel.toUi()
                    _state.update { s ->
                        s.copy(
                            productsSection = productsSectionUi,
                            currentSort = if (s.currentSort == SortUi.Empty) productsSectionUi.sorting.firstOrNull()
                                ?: SortUi.Empty.copy(name = productsSectionUi.sortingTitle) else s.currentSort,
                            uiState = ProductsCollectionUiState.Success
                        )
                    }
                }
            }.take(1).launchIn(viewModelScope)

    fun showSortOptionsBottomSheet() = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                showSortOptionsBottomSheet = true
            )
        }
    }

    fun closeSortOptionsBottomSheet() = viewModelScope.launch {
        _state.update { s ->
            s.copy(
                showSortOptionsBottomSheet = false
            )
        }
    }

    fun selectSort(sort: SortUi) = viewModelScope.launch {
        if (sort == stateSnapshot.currentSort) return@launch
        _state.update { s ->
            s.copy(
                currentSort = sort,
            )
        }
        fetchProducts()
        closeSortOptionsBottomSheet()
    }

    fun navigateBack() = viewModelScope.launch {
        _events.emit(ProductsCollectionEvent.GoBack)
    }

    fun switchLayout() = viewModelScope.launch {
        _state.update { s ->
            s.copy(isGridView = !s.isGridView)
        }
    }

    fun navigateToProductDetails(product: ProductUi) = viewModelScope.launch {
        _events.emit(ProductsCollectionEvent.GoToProductDetails(product.id))
    }

    fun changeProductFavorite(product: ProductUi) = viewModelScope.launch {
        favoritesManager.changeFavorite(product.id, !product.isFavorite)
    }

    fun navigateToProductAnalogs(product: ProductUi) = viewModelScope.launch {
        _events.emit(ProductsCollectionEvent.GoToProductAnalogs(product.id))
    }

    fun incrementProductToCart(product: ProductUi) = viewModelScope.launch {
        cartManager.change(product.id, product.cartQuantity + 1)
    }

    fun decrementProductToCart(product: ProductUi) = viewModelScope.launch {
        cartManager.change(product.id, product.cartQuantity - 1)
    }


}