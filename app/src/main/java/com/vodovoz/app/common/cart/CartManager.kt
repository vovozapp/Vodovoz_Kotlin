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

    suspend fun add(id: Long, oldCount: Int, newCount: Int, withUpdate: Boolean = true) {

        val isInCart = oldCount == 0

        updateCarts(id, newCount)

        runCatching {
            action(id = id, count = newCount, isInCart = isInCart)
            updateCartListState(withUpdate)
        }.onFailure {
            updateCarts(id, newCount)
        }
    }

    suspend fun clearCart() {
        carts.clear()
        cartsStateListener.emit(carts)
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

    private suspend fun action(id: Long, count: Int, isInCart: Boolean) {
        return if (!isInCart) {
            repository.changeProductsQuantityInCart(id, count)
            updateCarts(id, count)
        } else {
            repository.addProductToCart(id, count)
            updateCarts(id, count)
        }
    }

    private suspend fun updateCarts(id: Long, count: Int) {
        carts[id] = count
        cartsStateListener.emit(carts)
    }

}