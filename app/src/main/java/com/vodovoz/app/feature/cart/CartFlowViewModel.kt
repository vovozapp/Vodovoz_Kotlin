package com.vodovoz.app.feature.cart

import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.*
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.LocalSyncExtensions.syncFavoriteProducts
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.cart.CartResponseJsonParser.parseCartResponse
import com.vodovoz.app.data.parser.response.cart.ClearCartResponseJsonParser.parseClearCartResponse
import com.vodovoz.app.mapper.CartBundleMapper.mapUoUI
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.CartAvailableProducts
import com.vodovoz.app.feature.cart.viewholders.cartempty.CartEmpty
import com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts.CartNotAvailableProducts
import com.vodovoz.app.feature.cart.viewholders.carttotal.CartTotal
import com.vodovoz.app.feature.favorite.FavoriteFlowViewModel
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
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
    private val dataRepository: DataRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager
) : PagingContractViewModel<CartFlowViewModel.CartState, CartFlowViewModel.CartEvents>(CartState()) {

    init {
        viewModelScope.launch {
            cartManager
                .observeUpdateCartList()
                .collect {
                    if (it) {
                        refresh()
                        cartManager.updateCartListState(false)
                    }
                }
        }

        //accountManager.reportYandexMetrica("Зашел в корзину") //todo релиз
    }

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

    fun refreshIdle() {
        uiStateListener.value = state.copy(loadingPage = true, data = CartState())
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
                        val availableProducts = mappedData.availableProductUIList.reversed()
                        val calculatedPrices = calculatePrice(availableProducts)
                        cartManager.syncCart(
                            availableProducts,
                            calculatedPrices.total
                        )
                        state.copy(
                            data = state.data.copy(
                                coupon = coupon ?: "",
                                infoMessage = mappedData.infoMessage,
                                giftProductUIList = mappedData.giftProductUIList,
                                availableProducts = CartAvailableProducts(
                                    CART_AVAILABLE_PRODUCTS_ID,
                                    availableProducts,
                                    showCheckForm = availableProducts.any { it.depositPrice != 0 },
                                    showReturnBottleBtn = false
                                ),
                                notAvailableProducts = CartNotAvailableProducts(
                                    CART_NOT_AVAILABLE_PRODUCTS_ID,
                                    mappedData.notAvailableProductUIList
                                ),
                                total = CartTotal(CART_TOTAL_ID, calculatedPrices),
                                bestForYouTitle = HomeTitle(
                                    id = 1,
                                    type = HomeTitle.VIEWED_TITLE,
                                    name = "Лучшее для вас",
                                    showAll = false,
                                    showAllName = "СМ.ВСЕ",
                                    categoryProductsName = mappedData.bestForYouCategoryDetailUI?.name ?: ""
                                ),
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
                .catch {
                    debugLog { "fetch cart error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            flow { emit(repository.fetchClearCartResponse(action = "delkorzina")) }
                .flowOn(Dispatchers.IO)
                .onEach {
                    val response = it.parseClearCartResponse()
                    if (response is ResponseEntity.Success) {
                        uiStateListener.value = state.copy(data = CartState(), false)
                        cartManager.clearCart()
                        fetchCart(state.data.coupon) //todo
                    } else {
                        uiStateListener.value = state.copy(loadingPage = false)
                    }
                }
                .flowOn(Dispatchers.Default)
                .catch {
                    debugLog { "clear cart error ${it.localizedMessage}" }
                    uiStateListener.value =
                        state.copy(error = it.toErrorState(), loadingPage = false)
                }
                .collect()
        }
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

    fun navigateToOrderFragment() {
        viewModelScope.launch {
            val id = accountManager.fetchAccountId()
            if (id == null) {
                eventListener.emit(CartEvents.NavigateToProfile)
            } else {
                eventListener.emit(
                    CartEvents.NavigateToOrder(
                        prices = state.data.total?.prices,
                        cart = getCart(),
                        coupon = state.data.coupon
                    )
                )
            }
        }
    }

    fun navigateToGiftsBottomFragment() {
        viewModelScope.launch {
            val id = accountManager.fetchAccountId()
            if (id == null) {
                eventListener.emit(CartEvents.NavigateToProfile)
            } else {
                eventListener.emit(CartEvents.NavigateToGifts(state.data.giftProductUIList))
            }
        }
    }

    fun onPreOrderClick(id: Long, name: String, detailPicture: String) {
        viewModelScope.launch {
            val accountId = accountManager.fetchAccountId()
            if (accountId == null) {
                //eventListener.emit(CartEvents.NavigateToProfile)
                eventListener.emit(CartEvents.GoToPreOrder(id, name, detailPicture))
            } else {
                eventListener.emit(CartEvents.GoToPreOrder(id, name, detailPicture))
            }
        }
    }

    private fun getCart(): String {
        val cart = state.data.availableProducts?.items?.map { Pair(it.id, it.cartQuantity) }
        val result = StringBuilder()
        for (product in cart!!) {
            result.append(product.first).append(":").append(product.second).append(",")
        }
        return result.toString()
    }

    fun changeRating(productId: Long, rating: Float, oldRating: Float) {
        viewModelScope.launch {
            ratingProductManager.rate(productId, rating = rating, oldRating = oldRating)
        }
    }

    data class CartState(
        val coupon: String = "",
        val infoMessage: String = "",
        val giftProductUIList: List<ProductUI> = emptyList(),
        val availableProducts: CartAvailableProducts? = null,
        val notAvailableProducts: CartNotAvailableProducts? = null,
        val total: CartTotal? = null,
        val bestForYouTitle: HomeTitle? = null,
        val bestForYouProducts: CategoryDetailUI? = null,
        val cartEmpty: CartEmpty = CartEmpty(CART_EMPTY_ID)
    ) : State


    sealed class CartEvents : Event {

        data class NavigateToOrder(
            val prices: CalculatedPrices?,
            val cart: String,
            val coupon: String
        ) : CartEvents()

        data class NavigateToGifts(val list: List<ProductUI>) : CartEvents()
        object NavigateToProfile : CartEvents()
        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) : CartEvents()
    }

    companion object {
        private const val CART_EMPTY_ID = -1
        private const val CART_AVAILABLE_PRODUCTS_ID = 1
        private const val CART_NOT_AVAILABLE_PRODUCTS_ID = 2
        private const val CART_TOTAL_ID = 3
    }
}