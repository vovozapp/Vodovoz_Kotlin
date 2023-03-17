package com.vodovoz.app.common.cart

import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.local.LocalDataSource
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor(
    private val repository: MainRepository,
    private val localDataSource: LocalDataSource,
) {

    private val updateCartListListener = MutableStateFlow(false)

    fun observeUpdateCartList() = updateCartListListener.asStateFlow()

    fun updateCartListState(update: Boolean) {
        updateCartListListener.value = update
    }

    private val carts = ConcurrentHashMap<Long, CartMapState>()
    private val cartsStateListener = MutableSharedFlow<Map<Long, CartMapState>>(replay = 1)

    fun observeCarts() = cartsStateListener.asSharedFlow().filter { it.isNotEmpty() }

    suspend fun add(id: Long, count: Int, isInCart: Boolean, withUpdate: Boolean = true) {
        updateCartListState(withUpdate)
        updateCarts(id, count, !isInCart)

        kotlin.runCatching {
            action(id, count, isInCart)
        }.onFailure {
            updateCarts(id, count, isInCart)
        }
    }

    private suspend fun action(id: Long, count: Int, isInCart: Boolean) {
        return if (isInCart) {
            //repository.remove(id)
            updateCarts(id, count, false)
        } else {
            //repository.add(id)
            updateCarts(id, count, true)
        }
    }

    private suspend fun updateCarts(id: Long, count: Int, state: Boolean) {
        carts[id] = CartMapState(count, state)
        cartsStateListener.emit(carts)
    }


    data class CartMapState(
        val count: Int,
        val isInCart: Boolean
    )

}