package com.vodovoz.app.common.cart

import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
    private val tabManager: TabManager
) {

    private val updateCartListListener = MutableStateFlow(false)

    fun observeUpdateCartList() = updateCartListListener.asStateFlow()

    fun updateCartListState(update: Boolean) {
        updateCartListListener.value = update
    }

    private val carts = ConcurrentHashMap<Long, Int>()
    private val cartsStateListener = MutableSharedFlow<Map<Long, Int>>(replay = 1)

    fun observeCarts() = cartsStateListener.asSharedFlow().filter { it.isNotEmpty() }

    suspend fun add(id: Long, oldCount: Int, newCount: Int, withUpdate: Boolean = true, repeat: Boolean = false) {
        val isInCart = if (repeat) { true } else { oldCount == 0 }
        val plus = newCount >= oldCount

        updateCarts(id, newCount)

        runCatching {
            action(id = id, count = newCount, isInCart = isInCart, plus)
            updateCartListState(withUpdate)
        }.onFailure {
            tabManager.loadingAddToCart(false, plus = true)
            updateCarts(id, newCount)
        }
    }

    suspend fun clearCart() {
        carts.clear()
        cartsStateListener.emit(carts)
        updateCartListState(true)
        tabManager.clearBottomNavCartState()
    }

    suspend fun syncCart(list: List<ProductUI>, total: Int) {
        list.forEach {
            carts[it.id] = it.cartQuantity
        }
        tabManager.saveBottomNavCartState(
            carts.values.sum(),
            total
        )
        cartsStateListener.emit(carts)
    }

    private suspend fun action(id: Long, count: Int, isInCart: Boolean, plus: Boolean) {
        return if (!isInCart) {
            tabManager.loadingAddToCart(true, plus = plus)
            repository.changeProductsQuantityInCart(id, count)
            updateCarts(id, count)
        } else {
            tabManager.loadingAddToCart(true, plus = true)
            repository.addProductToCart(id, count)
            updateCarts(id, count)
        }
    }

    private suspend fun updateCarts(id: Long, count: Int) {
        carts[id] = count
        cartsStateListener.emit(carts)
    }

    //Service Details Products
    suspend fun addWithGift(id: Long, oldCount: Int, newCount: Int, withUpdate: Boolean = true, repeat: Boolean = false, giftId: String) {
        val plus = newCount >= oldCount

        updateCarts(id, newCount)

        runCatching {
            debugLog { "add with gift" }
            actionWithGift(id = id, count = newCount, plus, giftId)
            updateCartListState(withUpdate)
        }.onFailure {
            debugLog { "add with gift error ${it.localizedMessage}" }
            tabManager.loadingAddToCart(false, plus = true)
            updateCarts(id, newCount)
        }
    }

    private suspend fun actionWithGift(id: Long, count: Int, plus: Boolean, giftId: String) {
        val idWithGift = "$id-$count;$giftId"
        debugLog { "action add with gift $idWithGift" }
        tabManager.loadingAddToCart(true, plus = plus)
        repository.addProductFromServiceDetails(idWithGift)
        updateCarts(id, count)
    }

}