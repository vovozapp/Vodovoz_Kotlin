package com.vodovoz.app.feature.promotiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.map
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.parser.response.promotion.PromotionDetailResponseJsonParser
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.PromotionDetailsUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.design_system.model.withUpdatedCart
import com.vodovoz.app.design_system.model.withUpdatedFavorites
import com.vodovoz.app.design_system.model.withUpdatedLoading
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.ui.model.PromotionDetailUI
import com.vodovoz.app.ui.paging.PagingDataListener
import com.vodovoz.app.ui.paging.copy
import com.vodovoz.app.ui.paging.emptyCombinedLoadStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionDetailFlowViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val accountManager: AccountManager,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<PromotionDetailFlowViewModel.PromotionDetailFlowState, PromotionDetailFlowViewModel.PromotionDetailEvent>(
    PromotionDetailFlowState()
) {

    private var promotionId = savedState.get<Long>("promotionId")?.toInt() ?: kotlin.run {
        navigateBack()
        -1
    }

    private val pagingProductsListener = PagingDataListener<ProductUi>(
        onUpdateItems = { itemSnapshotList ->
            uiStateListener.updateData { s ->
                val pagedProducts = itemSnapshotList.mapNotNull { product -> product }
                s.copy(products = pagedProducts)
            }
        }
    )


    init {
        listenProductsLoadStates()
    }

    suspend fun listenProductLoadings() =
        uiStateListener.map { it.data.products }
            .distinctUntilChanged()
            .combine(cartManager.blockedProductsState) { _, blockedProducts ->
                blockedProducts
            }.collectLatest { blockedProducts ->
                uiStateListener.updateData { s ->
                    s.copy(
                        products = s.products.withUpdatedLoading(blockedProducts)
                    )
                }
            }

    suspend fun listenFavorites() = uiStateListener.map { it.data.products }
        .distinctUntilChanged()
        .combine(likeManager.observeLikes()) { _, favorites ->
            favorites
        }.collectLatest { favorites ->
            uiStateListener.updateData { s ->
                s.copy(
                    products = s.products.withUpdatedFavorites(favorites)
                )
            }
        }

    suspend fun listenCart() = uiStateListener.map { it.data.products }
        .distinctUntilChanged()
        .combine(cartManager.observeCarts()) { _, cart ->
            cart
        }.collectLatest { cart ->
            uiStateListener.updateData { s ->
                s.copy(
                    products = s.products.withUpdatedCart(cart)
                )
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


    fun decrementProductToCart(product: ProductUi) = viewModelScope.launch {
        cartManager.change(product.id, product.cartQuantity - 1)
    }

    fun incrementProductToCart(product: ProductUi) = viewModelScope.launch {
        cartManager.change(product.id, product.cartQuantity + 1)
    }

    fun navigateToProductAnalogs(product: ProductUi) = viewModelScope.launch {
        eventListener.emit(PromotionDetailEvent.GoToProductAnalogs(product.id))
    }


    private fun fetchPromotionDetails() {
        uiStateListener.updateData { s ->
            s.copy(uiState = UiState.Loading)
        }
        vodovozServiceRepository.getPromotionDetails(promotionId)
            .onEach { promotionDetailsResult ->
                promotionDetailsResult.onSuccess { titleAndPromotionDetails ->


                    uiStateListener.updateData { s ->
                        s.copy(
                            promotionDetails = titleAndPromotionDetails.second.toUi(),
                            productsTitle = titleAndPromotionDetails.first.title,
                            uiState = UiState.Success
                        )
                    }

                    vodovozServiceRepository.getPromotionDetailsProductsPaged(promotionId)
                        .onEach { pagingData ->
                            val pg = pagingData.map { productModel ->
                                productModel.toUi()
                            }
                            pagingProductsListener.collectPagingData(pg)
                        }.launchIn(viewModelScope)

                }.onFailure {
                    uiStateListener.updateData { s ->
                        s.copy(uiState = UiState.Error)
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun navigateBack() = viewModelScope.launch {
        eventListener.emit(PromotionDetailEvent.GoBack)
    }

    fun firstLoadSorted() {
        fetchPromotionDetails()
        if (!state.isFirstLoad) {
            uiStateListener.value =
                state.copy(isFirstLoad = true, loadingPage = true)
            //todo - uncomment or delete
            //fetchPromotionDetails()
        }
    }

    fun refreshSorted() {
        uiStateListener.value =
            state.copy(loadingPage = true, page = 1, loadMore = false, bottomItem = null)
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()

    fun changeCart(productId: Long, quantity: Int, oldQuan: Int) {
        viewModelScope.launch {
            cartManager.add(id = productId, oldCount = oldQuan, newCount = quantity)
        }
    }

    fun changeRating(productId: Long, rating: Float, oldRating: Float) {
        viewModelScope.launch {
            ratingProductManager.rate(productId, rating = rating, oldRating = oldRating)
        }
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            likeManager.like(productId, !isFavorite)
        }
    }

    fun navigateToWebView(url: String) = viewModelScope.launch {
        eventListener.emit(PromotionDetailEvent.GoToWebView(url))
    }

    fun navigateToProductDetails(product: ProductUi) = viewModelScope.launch {
        eventListener.emit(PromotionDetailEvent.GoToProductDetails(product.id))
    }

    fun changeProductFavorite(product: ProductUi) = viewModelScope.launch {
        likeManager.changeFavorite(product.id, !product.isFavorite)
    }

    fun notifyPagingProducts(index: Int) = viewModelScope.launch {
        kotlin.runCatching {
            pagingProductsListener[index]
        }
    }

    data class PromotionDetailFlowState(
        val items: PromotionDetailUI? = null,
        val errorItem: PromotionDetailResponseJsonParser.PromotionDetailErrorUI? = null,
        val promotionDetails: PromotionDetailsUi = PromotionDetailsUi.Empty,
        val productsTitle: String = "",
        val products: List<ProductUi> = emptyList(),
        val productsLoadStates: CombinedLoadStates = emptyCombinedLoadStates,
        val uiState: UiState = UiState.Loading,
    ) : State

    sealed interface UiState {
        data object Loading : UiState
        data object Error : UiState
        data object Success : UiState
    }

    sealed class PromotionDetailEvent : Event {
        data class GoToWebView(val url: String) : PromotionDetailEvent()
        data class GoToProductAnalogs(val productId: Long) : PromotionDetailEvent()
        data class GoToProductDetails(val productId: Long) : PromotionDetailEvent()

        data object GoBack : PromotionDetailEvent()

    }
}