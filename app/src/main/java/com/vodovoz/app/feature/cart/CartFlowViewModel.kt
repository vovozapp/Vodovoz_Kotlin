package com.vodovoz.app.feature.cart

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.common.cart.CartManager
import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.common.content.updateData
import com.vodovoz.app.common.like.LikeManager
import com.vodovoz.app.common.product.rating.RatingProductManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.cart.MessageTextBasket
import com.vodovoz.app.design_system.model.VodovozPlaceholderUi
import com.vodovoz.app.design_system.model.toUi
import com.vodovoz.app.domain.general.model.EmptyResultException
import com.vodovoz.app.domain.general.model.cart.CartOrderSummaryUi
import com.vodovoz.app.domain.general.model.cart.toUi
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.feature.cart.model.CartButtonUi
import com.vodovoz.app.feature.cart.model.CartItemUi
import com.vodovoz.app.feature.cart.model.CartPresentItemUi
import com.vodovoz.app.feature.cart.model.CartPresentPopupWindowUi
import com.vodovoz.app.feature.cart.model.CartPresentUi
import com.vodovoz.app.feature.cart.model.CartPromoButtonUi
import com.vodovoz.app.feature.cart.model.mapToUi
import com.vodovoz.app.feature.cart.model.toUi
import com.vodovoz.app.feature.cart.model.withUpdatedCart
import com.vodovoz.app.feature.cart.model.withUpdatedFavorites
import com.vodovoz.app.feature.cart.viewholders.cartavailableproducts.CartAvailableProducts
import com.vodovoz.app.feature.cart.viewholders.cartempty.CartEmpty
import com.vodovoz.app.feature.cart.viewholders.cartnotavailableproducts.CartNotAvailableProducts
import com.vodovoz.app.feature.cart.viewholders.carttotal.CartTotal
import com.vodovoz.app.feature.home.viewholders.hometitle.HomeTitle
import com.vodovoz.app.mapper.CartBundleMapper.mapUoUI
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.ui.model.custom.GiftProductUI
import com.vodovoz.app.util.CalculatedPrices
import com.vodovoz.app.util.calculatePrice
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartFlowViewModel @Inject constructor(
    private val repository: MainRepository,
    private val cartManager: CartManager,
    private val likeManager: LikeManager,
    private val ratingProductManager: RatingProductManager,
    private val accountManager: AccountManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : PagingContractViewModel<CartFlowViewModel.CartState, CartFlowViewModel.CartEvents>(CartState()) {

    init {
        viewModelScope.launch { listenCart() }
        viewModelScope.launch {
            cartManager.observeUpdateCartList().collectLatest { newCart ->
                if (newCart) {
                    refresh()
                    cartManager.updateCartListState(false)
                }
            }
        }
    }

    suspend fun listenCart() =
        uiStateListener.map { it.data.cartItems }.combine(cartManager.observeCarts()) { _, cart ->
            cart
        }.collectLatest { cart ->
            uiStateListener.updateData { s ->
                s.copy(cartItems = s.cartItems.withUpdatedCart(cart))
            }
        }


    suspend fun listenFavorites() {
        uiStateListener.map { it.data.cartItems }
            .combine(likeManager.observeLikes()) { _, favorites ->
                favorites
            }.collectLatest { favorites ->
                uiStateListener.updateData { s ->
                    s.copy(cartItems = s.cartItems.withUpdatedFavorites(favorites))
                }
            }
    }

    fun fetchCartDetails() = viewModelScope.launch {
        if (dataState.uiState is CartUiState.Empty || dataState.uiState == CartUiState.Error) {
            uiStateListener.updateData { s -> s.copy(uiState = CartUiState.Loading) }
        }

        val cartDetailsResult = vodovozServiceRepository.getCartDetails(
            dataState.promoCode
        ).singleResult()

        cartDetailsResult.onSuccess { cartDetails ->
            val cartItems = cartDetails.items.mapToUi()
            uiStateListener.updateData { s ->
                s.copy(
                    title = cartDetails.title,
                    countText = cartDetails.countText,
                    cartItems = cartDetails.items.mapToUi(),
                    present = cartDetails.present?.toUi(),
                    bottlesButton = cartDetails.bottlesButton?.toUi(),
                    promotionalCodeButton = cartDetails.promotionalCodeButton?.toUi(),
                    presentButton = cartDetails.presentButton?.toUi(),
                    uiState = CartUiState.Cart,
                    orderSummary = cartDetails.orderSummary.toUi()
                )
            }


            cartManager.syncCart(
                cartItems.associate { item ->
                    item.productId to item.quantity
                }
            )

        }.onFailure { t ->
            val uiState = when (t) {
                is EmptyResultException -> {
                    CartUiState.Empty(errorData = t.errorData?.toUi() ?: VodovozPlaceholderUi.Empty)
                }

                else -> {
                    CartUiState.Error
                }
            }
            uiStateListener.updateData { s ->
                s.copy(uiState = uiState)
            }
        }
    }

    fun firstLoad() {
        if (!state.isFirstLoad) {
            uiStateListener.value = state.copy(isFirstLoad = true, loadingPage = true)
            fetchCartDetails()
            fetchCart()
        }
    }

    fun refresh() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showRefreshIndicator = true)
        }
        fetchCartDetails().join()
        uiStateListener.updateData { s ->
            s.copy(showRefreshIndicator = false)
        }
    }

    fun refreshIdle() {
        uiStateListener.value = state.copy(loadingPage = true, data = CartState())
        fetchCartDetails()
        fetchCart(state.data.coupon)
    }

    fun fetchCart(coupon: String? = null) {

        //todo - remove return
        return
        viewModelScope.launch {
            val userId = accountManager.fetchAccountId()
            uiStateListener.value = state.copy(loadingPage = true)
            flow {
                emit(
                    repository.fetchCartResponse(
                        userId = userId,
                        coupon = coupon,
                    )
                )
            }
                .onEach { response ->
                    uiStateListener.value = if (response is ResponseEntity.Success) {
                        val mappedData = response.data.mapUoUI()
                        val availableProducts = mappedData.availableProductUIList.reversed()
                        val calculatedPrices = calculatePrice(availableProducts)
                        if (availableProducts.isEmpty() && !cartManager.isCartEmpty()) {
                            //todo - uncomment
                            cartManager.clearCart()
                        } else {
                            //todo - uncomment
                            cartManager.syncCart(
                                availableProducts
                            )
                        }
                        state.copy(
                            data = state.data.copy(
                                coupon = coupon ?: "",
                                infoMessage = mappedData.infoMessage,
                                giftMessageBottom = if (coupon.isNullOrEmpty()) {
                                    mappedData.giftMessageBottom?.copy(
                                        title = mappedData.giftTitleBottom
                                    )
                                } else {
                                    state.data.giftMessageBottom
                                },
                                giftProductUI = mappedData.giftProductUI,
                                availableProducts = CartAvailableProducts(
                                    CART_AVAILABLE_PRODUCTS_ID,
                                    availableProducts,
                                    showCheckForm = availableProducts.any { it.depositPrice != 0 } && isCountOfBottlesLessThenCountOfWater(
                                        availableProducts
                                    ),
                                    showReturnBottleBtn = false,
                                    giftMessage = mappedData.giftMessage
                                ),
                                notAvailableProducts = CartNotAvailableProducts(
                                    CART_NOT_AVAILABLE_PRODUCTS_ID,
                                    mappedData.notAvailableProductUIList,
                                    giftMessage = mappedData.giftMessage
                                ),
                                total = CartTotal(
                                    CART_TOTAL_ID,
                                    coupon ?: state.data.coupon,
                                    calculatedPrices
                                ),
                                bestForYouTitle = HomeTitle(
                                    id = 1,
                                    type = HomeTitle.VIEWED_TITLE,
                                    name = "Лучшее для вас",
                                    showAll = false,
                                    showAllName = "СМ.ВСЕ",
                                    categoryProductsName = mappedData.bestForYouCategoryDetailUI?.name
                                        ?: ""
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

    fun showClearCartDialog() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showClearCartDialog = true)
        }
    }

    fun clearCart() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(blockCart = true, showClearCartDialog = false)
        }
        val clearCartResult = vodovozServiceRepository.clearCart().singleResult()

        clearCartResult.onSuccess {
            cartManager.clearCart()
        }

        uiStateListener.updateData { s ->
            s.copy(blockCart = false)
        }

//        viewModelScope.launch {
//            flow { emit(repository.fetchClearCartResponse(action = "delkorzina")) }
//                .onEach { response ->
//                    if (response is ResponseEntity.Success) {
//                        uiStateListener.value = state.copy(data = CartState(), false)
//                        cartManager.clearCart()
//                        fetchCart(state.data.coupon) //todo
//                    } else {
//                        uiStateListener.value = state.copy(loadingPage = false)
//                    }
//                }
//                .flowOn(Dispatchers.Default)
//                .catch {
//                    debugLog { "clear cart error ${it.localizedMessage}" }
//                    uiStateListener.value =
//                        state.copy(error = it.toErrorState(), loadingPage = false)
//                }
//                .collect()
//        }
    }

    fun isLoginAlready() = accountManager.isAlreadyLogin()

    fun changeCart(productId: Long, quantity: Int, oldQuan: Int) {
        viewModelScope.launch {
            var quant = quantity
            state.data.availableProducts?.items?.let { productList ->
                val product = productList.find { it.id == productId }
                if (product != null && product.isBottle) {
                    if (oldQuan < quant && !isCountOfBottlesLessThenCountOfWater(productList)) {
                        quant = oldQuan
                    }
                }
            }
            cartManager.add(id = productId, oldCount = oldQuan, newCount = quant)
        }
    }

    private fun isCountOfBottlesLessThenCountOfWater(productList: List<ProductUI>): Boolean {
        val sizeOfWater = productList
            .filter { it.depositPrice > 0 && !it.isBottle }
            .sumOf { it.cartQuantity }
        val sizeOfBottles = productList
            .filter { it.isBottle }
            .sumOf {
                if (it.oldQuantity != 0) {
                    it.oldQuantity
                } else {
                    it.cartQuantity
                }
            }
        if (sizeOfBottles >= sizeOfWater) {
            return false
        }
        return true
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
                //eventListener.emit(CartEvents.NavigateToGifts(state.data.giftProductUI))
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

    fun clearInfoMessage() {
        uiStateListener.value = state.copy(data = state.data.copy(infoMessage = null))
    }

    fun clearCoupon() {
        uiStateListener.value = state.copy(data = state.data.copy(coupon = ""))
    }

    fun navigateToProductDetails(cartItem: CartItemUi) = viewModelScope.launch {
        eventListener.emit(CartEvents.GoToProductDetails(cartItem.productId))
    }

    fun incrementCartItem(cartItem: CartItemUi) = viewModelScope.launch {
        cartManager.change(cartItem.productId, cartItem.quantity + 1)
    }

    fun decrementCartItem(cartItem: CartItemUi) = viewModelScope.launch {
        cartManager.change(cartItem.productId, cartItem.quantity - 1)
    }

    fun changeFavorite(cartItem: CartItemUi) = viewModelScope.launch {
        likeManager.changeFavorite(cartItem.productId, !cartItem.isFavorite)
    }

    fun navigateToCatalog() = viewModelScope.launch {
        eventListener.emit(CartEvents.GoToCatalog)
    }

    fun closeClearCartDialog() = viewModelScope.launch {
        uiStateListener.updateData { s -> s.copy(showClearCartDialog = false) }
    }

    fun showTrashDialog(cartItem: CartItemUi) = viewModelScope.launch {
        //todo - update delete logic by ZAPRET_FISHKAM
        uiStateListener.updateData { s ->
            s.copy(
                showRemoveItemDialog = true,
                currentRemoveItem = cartItem
            )
        }
    }

    fun closeTrashDialog() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showRemoveItemDialog = false)
        }
    }

    fun removeCartItem(currentRemoveItem: CartItemUi) = viewModelScope.launch {
        uiStateListener.updateData { s -> s.copy(blockCart = true, showRemoveItemDialog = false) }

        vodovozServiceRepository.updateProductInCart(currentRemoveItem.productId, 0).singleResult()
        fetchCartDetails().join()

        uiStateListener.updateData { s ->
            s.copy(blockCart = false)
        }
    }

    fun showPromotionCodeBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showPromotionCodeBottomSheet = true)
        }
    }

    fun changePromoCode(newValue: String) = viewModelScope.launch {
        uiStateListener.updateData { s ->

            val promoButton = s.promotionalCodeButton
            s.copy(
                promoCode = newValue,
                promotionalCodeButton = promoButton?.copy(
                    popupWindow = promoButton.popupWindow.copy(
                        errorText = null
                    )
                )
            )
        }
    }

    fun closePromoCodeBottomSheet() = viewModelScope.launch {
        uiStateListener.updateData { s ->
            s.copy(showPromotionCodeBottomSheet = false, promoCode = "")
        }
    }

    fun applyPromoCode() = viewModelScope.launch {
        fetchCartDetails().join()

        val correctCoupon = dataState.promotionalCodeButton?.coupon
        if (correctCoupon.isNullOrEmpty()) {
            return@launch
        }

        uiStateListener.updateData { s ->
            s.copy(showPromotionCodeBottomSheet = false, promoCode = correctCoupon)
        }
    }

    fun navigateToGifts() = viewModelScope.launch {
        val userId = accountManager.fetchAccountId()
        if (userId == null) {
            eventListener.emit(CartEvents.NavigateToProfile)
        } else {
            val present = dataState.present ?: return@launch
            val popupWindow = dataState.present?.popupWindow ?: return@launch
            if (popupWindow.items.isEmpty()) return@launch

            eventListener.emit(CartEvents.NavigateToGifts(present, popupWindow))
        }
    }

    fun addGiftToCart(presentItem: CartPresentItemUi) = viewModelScope.launch {
        uiStateListener.updateData { s -> s.copy(blockCart = true) }

        vodovozServiceRepository.addProductToCart(presentItem.id, 1).singleResult()
        fetchCartDetails().join()

        uiStateListener.updateData { s -> s.copy(blockCart = false) }

    }

    fun navigateToAllBottles() = viewModelScope.launch {
        eventListener.emit(CartEvents.GoToAllBottles)
    }

    @Immutable
    data class CartState(
        val coupon: String = "",
        val infoMessage: MessageTextBasket? = null,
        val giftMessageBottom: MessageTextBasket? = null,
        val giftProductUI: GiftProductUI? = null,
        val availableProducts: CartAvailableProducts? = null,
        val notAvailableProducts: CartNotAvailableProducts? = null,
        val total: CartTotal? = null,
        val bestForYouTitle: HomeTitle? = null,
        val bestForYouProducts: CategoryDetailUI? = null,
        val cartEmpty: CartEmpty = CartEmpty(CART_EMPTY_ID),

        val title: String = "",
        val countText: String = "",
        val uiState: CartUiState = CartUiState.Loading,
        val cartItems: List<CartItemUi> = emptyList(),
        val present: CartPresentUi? = null,
        val bottlesButton: CartButtonUi? = null,
        val promotionalCodeButton: CartPromoButtonUi? = null,
        val presentButton: CartButtonUi? = null,
        val showClearCartDialog: Boolean = false,
        val showRemoveItemDialog: Boolean = false,
        val currentRemoveItem: CartItemUi? = null,
        val showRefreshIndicator: Boolean = false,
        val blockCart: Boolean = false,
        val orderSummary: CartOrderSummaryUi = CartOrderSummaryUi.Empty,
        val showPromotionCodeBottomSheet: Boolean = false,
        val promoCode: String = "",
    ) : State {
    }

    sealed interface CartUiState {
        data object Loading : CartUiState
        data object Cart : CartUiState
        data class Empty(val errorData: VodovozPlaceholderUi) : CartUiState
        data object Error : CartUiState
    }


    sealed class CartEvents : Event {

        data class NavigateToOrder(
            val prices: CalculatedPrices?,
            val cart: String,
            val coupon: String,
        ) : CartEvents()

        data class NavigateToGifts(
            val present: CartPresentUi? = null,
            val popupWindow: CartPresentPopupWindowUi,
        ) : CartEvents()

        data object NavigateToProfile : CartEvents()
        data object GoToCatalog : CartEvents()
        data object GoToAllBottles : CartEvents()

        data class GoToPreOrder(val id: Long, val name: String, val detailPicture: String) :
            CartEvents()

        data class GoToProductDetails(val productId: Long) : CartEvents()
    }

    companion object {
        private const val CART_EMPTY_ID = -1
        private const val CART_AVAILABLE_PRODUCTS_ID = 1
        private const val CART_NOT_AVAILABLE_PRODUCTS_ID = 2
        private const val CART_TOTAL_ID = 3
    }
}