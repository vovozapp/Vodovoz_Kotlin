package com.vodovoz.app.ui.fragment.cart

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.cart.CartResponseJsonParser.parseCartResponse
import com.vodovoz.app.data.parser.response.cart.ClearCartResponseJsonParser.parseClearCartResponse
import com.vodovoz.app.mapper.CartBundleMapper.mapUoUI
import com.vodovoz.app.ui.base.content.PagingStateViewModel
import com.vodovoz.app.ui.base.content.State
import com.vodovoz.app.ui.base.content.toErrorState
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

    fun fetchCart(coupon: String? = null) {
        viewModelScope.launch {
            flow { emit(repository.fetchCartResponse(
                action = "getbasket",
                userId = localDataSource.fetchUserId(),
                coupon = coupon
            ))}
                .catch {
                    debugLog { "fetch cart error ${it.localizedMessage}" }
                    uiStateListener.value = state.copy(error = it.toErrorState(), loadingPage = false)
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
                            cartBundleEntity.availableProductEntityList.syncFavoriteProducts(localDataSource)
                            cartBundleEntity.notAvailableProductEntityList.syncFavoriteProducts(localDataSource)
                            localDataSource.fetchCart()
                        }
                        val mappedData = response.data.mapUoUI()
                        state.copy(data = state.data.copy(items = mappedData, calculatedPrices = calculatePrice(mappedData.availableProductUIList), coupon = coupon.takeIf { mappedData.infoMessage.isEmpty() } ?: ""))
                    } else {
                        state.copy(loadingPage = false, data = state.data.copy(items = null, calculatedPrices = null, coupon = ""))
                    }
                }
                .flowOn(Dispatchers.Default)
                .collect()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            flow { emit(repository.fetchClearCartResponse(
                action = "delkorzina"
            )) }
                .catch {
                    debugLog { "clear cart error ${it.localizedMessage}" }
                    uiStateListener.value = state.copy(error = it.toErrorState(), loadingPage = false)
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

            when(oldQuantity) {
                null -> addToCart(productId, quantity)
                else -> changeProductQuantityInCart(productId, quantity)
            }
        }
    }

    private fun addToCart(productId: Long, quantity: Int) {
        viewModelScope.launch {
            flow { emit(repository.addProductToCart(
                productId = productId,
                quantity = quantity
            ))}
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
            flow { emit(repository.changeProductsQuantityInCart(
                productId = productId,
                quantity = quantity
            ))}
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
            when(isFavorite) {
                true -> addToFavorite(listOf(productId))
                false -> removeFromFavorite(productId = productId)
            }
        }
    }

    private fun addToFavorite(productIdList: List<Long>) {
        viewModelScope.launch {

            val userId = localDataSource.fetchUserId()

            when(isLoginAlready()) {
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

            when(isLoginAlready()) {
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
        val items: CartBundleUI? = null,
        val calculatedPrices: CalculatedPrices? = null,
        val coupon: String? = ""
    ) : State
}