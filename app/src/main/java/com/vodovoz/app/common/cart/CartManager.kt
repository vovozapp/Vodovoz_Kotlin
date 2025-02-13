package com.vodovoz.app.common.cart

import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor(
    private val repository: MainRepository,
    private val tabManager: TabManager,
) {

    private val updateCartListListener = MutableStateFlow(false)

    fun observeUpdateCartList() = updateCartListListener.asStateFlow()

    fun updateCartListState(update: Boolean) {
        updateCartListListener.value = update
    }

    private val carts = ConcurrentHashMap<Long, Int>()
    private val cartsStateListener = MutableSharedFlow<Map<Long, Int>>(replay = 1)

    fun observeCarts() = cartsStateListener.asSharedFlow().filter { it.isNotEmpty() }

    suspend fun add(
        id: Long,
        oldCount: Int,
        newCount: Int,
        withUpdate: Boolean = true,
    ) {
        val isInCart =  oldCount != 0

        //val plus = newCount >= oldCount

        updateCarts(id, newCount)

        runCatching {
            action(id = id, count = newCount, isInCart = isInCart/*, plus*/)
            updateCartListState(withUpdate)
        }.onFailure {
            //tabManager.loadingAddToCart(false, plus = true)
            updateCarts(id, newCount)
        }
    }

    suspend fun clearCart() {
        carts.clear()
        cartsStateListener.emit(carts)
        updateCartListState(true)
        tabManager.clearBottomNavCartState()
    }

    fun isCartEmpty() = carts.isEmpty()

    suspend fun syncCart(list: List<ProductUI>) {
        list.forEach {
            carts[it.id] = it.cartQuantity
        }
        tabManager.saveBottomNavCartState()
        cartsStateListener.emit(carts)
    }

    private suspend fun action(id: Long, count: Int, isInCart: Boolean/*, plus: Boolean*/) {
        if (!isInCart) {
            //tabManager.loadingAddToCart(true, plus = plus)
            repository.addProductToCart(id, count)
       } else {
            repository.changeProductsQuantityInCart(id, count)
            //tabManager.loadingAddToCart(true, plus = true)
        }
        updateCarts(id, count)
    }

    private suspend fun updateCarts(id: Long, count: Int) {
        carts[id] = count
        cartsStateListener.emit(carts)
    }

    //Service Details Products
    suspend fun addWithGift(
        id: Long,
        newCount: Int,
        withUpdate: Boolean = true,
        giftId: String,
    ) {
        //val plus = newCount >= oldCount

        updateCarts(id, newCount)

        runCatching {
            debugLog { "add with gift" }
            actionWithGift(id = id, count = newCount, /*plus,*/ giftId)
            updateCartListState(withUpdate)
        }.onFailure {
            debugLog { "add with gift error ${it.localizedMessage}" }
           // tabManager.loadingAddToCart(false, plus = true)
            updateCarts(id, newCount)
        }
    }

    private suspend fun actionWithGift(id: Long, count: Int,/* plus: Boolean,*/ giftId: String) {
        val idWithGift = "$id-$count;$giftId"
        debugLog { "action add with gift $idWithGift" }
        //tabManager.loadingAddToCart(true, plus = plus)
        repository.addProductFromServiceDetails(idWithGift)
        updateCarts(id, count)
    }

}