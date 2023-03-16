package com.vodovoz.app.feature.cart

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.cart.CartResponseJsonParser.parseCartResponse
import com.vodovoz.app.data.parser.response.cart.ClearCartResponseJsonParser.parseClearCartResponse
import com.vodovoz.app.mapper.CartBundleMapper.mapUoUI
import com.vodovoz.app.common.content.PagingStateViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.CartAvailableProducts
import com.vodovoz.app.feature.cart.viewholders.cartempty.CartEmpty
import com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts.CartNotAvailableProducts
import com.vodovoz.app.feature.cart.viewholders.carttotal.CartTotal
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.custom.CartBundleUI
import com.vodovoz.app.util.CalculatedPrices
import com.vodovoz.app.util.calculatePrice
import com.vodovoz.app.util.extensions.debugLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val dataRepository: DataRepository
) : PagingStateViewModel<CartFlowViewModel.CartState>(CartState()) {

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchCart()
        }
    }

    fun refresh() {
        uiStateListener.value = state.copy(loadingPage = true)
        fetchCart()
    }

    fun fetchCart(coupon: String? = null) {
        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchCartResponse(
                        action = "getbasket",
                        userId = localDataSource.fetchUserId(),
                        coupon = coupon
                    )
                )
            }
                .catch {
                    debugLog { "fetch cart error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseCartResponse()
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        response.data.let { cartBundleEntity ->
                            localDataSource.clearCart()
                            cartBundleEntity.availableProductEntityList.forEach { productUI ->
                                localDataSource.changeProductQuantityInCart(
                                    productId = productUI.id,
                                    quantity = productUI.cartQuantity
                                )
                            }
                            cartBundleEntity.availableProductEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            cartBundleEntity.notAvailableProductEntityList.syncFavoriteProducts(
                                localDataSource
                            )
                            localDataSource.fetchCart()
                        }
                        val mappedData = response.data.mapUoUI()
                        state.copy(
                            data = state.data.copy(
                                coupon = coupon ?: "",
                                infoMessage = mappedData.infoMessage,
                                giftProductUIList = mappedData.giftProductUIList,
                                availableProducts = CartAvailableProducts(
                                    CART_AVAILABLE_PRODUCTS_ID,
                                    mappedData.availableProductUIList,
                                    showCheckForm = mappedData.availableProductUIList.any { it.depositPrice != 0 },
                                    showReturnBottleBtn = false
                                ),
                                notAvailableProducts = CartNotAvailableProducts(
                                    CART_NOT_AVAILABLE_PRODUCTS_ID, mappedData.notAvailableProductUIList),
                                total = CartTotal(CART_TOTAL_ID, calculatePrice(mappedData.availableProductUIList)),
                                bestForYouProducts = mappedData.bestForYouCategoryDetailUI
                            ),
                            loadingPage = false,
                            error = null
                        )
                    } else {
                        state.copy(loadingPage = false, error = ErrorState.Error())
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            flow {
                emit(
                    repository.fetchClearCartResponse(
                        action = "delkorzina"
                    )
                )
            }
                .catch {
                    debugLog { "clear cart error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .flowOn(Dispatchers.IO)
                .collect {
                    val response = it.parseClearCartResponse()
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(data = CartState(), false)
                    } else {
                        uiStateListener.value = state.copy(loadingPage = false)
                    }
                }
        }
    }

    fun isLoginAlready() = dataRepository.isAlreadyLogin()

    fun changeCart(productId: Long, quantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            val cart = localDataSource.fetchCart()
            val oldQuantity = cart[productId]

            when (oldQuantity) {
                null -> addToCart(productId, quantity)
                else -> changeProductQuantityInCart(productId, quantity)
            }
        }
    }

    private fun addToCart(productId: Long, quantity: Int) {
        viewModelScope.launch {
            flow {
                emit(
                    repository.addProductToCart(
                        productId = productId,
                        quantity = quantity
                    )
                )
            }
                .catch { debugLog { "add to cart error ${it.localizedMessage}" } }
                .flowOn(Dispatchers.IO)
                .collect {
                    localDataSource.changeProductQuantityInCart(
                        productId = productId,
                        quantity = quantity
                    )
                }
        }
    }

    private fun changeProductQuantityInCart(productId: Long, quantity: Int) {
        viewModelScope.launch {
            flow {
                emit(
                    repository.changeProductsQuantityInCart(
                        productId = productId,
                        quantity = quantity
                    )
                )
            }
                .catch { debugLog { "change cart error ${it.localizedMessage}" } }
                .flowOn(Dispatchers.IO)
                .collect {
                    localDataSource.changeProductQuantityInCart(
                        productId = productId,
                        quantity = quantity
                    )
                }
        }
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            when (isFavorite) {
                true -> addToFavorite(listOf(productId))
                false -> removeFromFavorite(productId = productId)
            }
        }
    }

    private fun addToFavorite(productIdList: List<Long>) {
        viewModelScope.launch {

            val userId = localDataSource.fetchUserId()

            when (isLoginAlready()) {
                false -> {
                    localDataSource.changeFavoriteStatus(
                        pairList = mutableListOf<Pair<Long, Boolean>>().apply {
                            productIdList.forEach { add(Pair(it, true)) }
                        }.toList()
                    )
                }
                true -> {
                    flow { emit(repository.addToFavorite(productIdList, userId!!)) }
                        .catch { debugLog { "add to fav error ${it.localizedMessage}" } }
                        .flowOn(Dispatchers.IO)
                        .collect {
                            localDataSource.changeFavoriteStatus(
                                pairList = mutableListOf<Pair<Long, Boolean>>().apply {
                                    productIdList.forEach { add(Pair(it, true)) }
                                }.toList()
                            )
                        }
                }
            }
        }
    }

    private fun removeFromFavorite(productId: Long) {
        viewModelScope.launch {

            val userId = localDataSource.fetchUserId()

            when (isLoginAlready()) {
                false -> {
                    localDataSource.changeFavoriteStatus(listOf(Pair(productId, false)))
                }
                true -> {
                    flow { emit(repository.removeFromFavorite(productId, userId!!)) }
                        .catch { debugLog { "remove from fav error ${it.localizedMessage}" } }
                        .flowOn(Dispatchers.IO)
                        .collect {
                            localDataSource.changeFavoriteStatus(listOf(Pair(productId, false)))
                        }
                }
            }
        }
    }


    data class CartState(
        val coupon: String = "",
        val infoMessage: String = "",
        val giftProductUIList: List<ProductUI> = emptyList(),
        val availableProducts: CartAvailableProducts? = null,
        val notAvailableProducts: CartNotAvailableProducts? = null,
        val total: CartTotal? = null,
        val bestForYouProducts: CategoryDetailUI? = null,
        val cartEmpty: CartEmpty = CartEmpty(CART_EMPTY_ID)
    ) : State

    companion object {
        private const val CART_EMPTY_ID = -1
        private const val CART_AVAILABLE_PRODUCTS_ID = 1
        private const val CART_NOT_AVAILABLE_PRODUCTS_ID = 2
        private const val CART_TOTAL_ID = 3
    }
}