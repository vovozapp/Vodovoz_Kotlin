package com.vodovoz.app.common.cart

import com.vodovoz.app.common.tab.TabManager
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.extensions.singleResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManager @Inject constructor(
    private val repository: MainRepository,
    private val tabManager: TabManager,
    private val vodovozServiceRepository: VodovozServiceRepository,
) {

    private val cartMutex = Mutex()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun observeUpdateCartList() = updateCartListListener.asStateFlow()

    fun updateCartListState(update: Boolean) {
        updateCartListListener.value = update
    }

    private val updateCartListListener = MutableStateFlow(false)
    private val carts = ConcurrentHashMap<Long, Int>()
    private val firstCart = mutableMapOf<Long, Int>()


    private val cartsStateListener = MutableSharedFlow<Map<Long, Int>>(replay = 1)
    private val _blockedProductsState = MutableStateFlow(emptySet<Long>())
    val blockedProductsState = _blockedProductsState.asStateFlow()
    private var cartVersion = 0


    fun observeCarts() = cartsStateListener.asSharedFlow()

    suspend fun change(productId: Long, count: Int) = coroutineScope.launch {
        val currentCartVersion = cartMutex.withLock {
            if (_blockedProductsState.value.contains(productId) || count < 0) return@launch
            val cartBeforeUpdate = carts.toMap()
            updateCarts(productId, count)
            if (firstCart.isEmpty()) {
                firstCart.putAll(cartBeforeUpdate)
            }
            return@withLock ++cartVersion
        }

        delay(300L)

        val (currentFirstCart, cartChanges) = cartMutex.withLock {
            val currentCart: Map<Long, Int> = carts

            if (currentCartVersion < cartVersion) return@launch

            val firstCartCopy = firstCart.toMap()
            firstCart.clear()

            val cartChanges = currentCart.filter { (key, value) ->
                val oldValue = firstCartCopy[key]
                value != oldValue
            }

            if (cartChanges.isEmpty()) return@launch

            _blockedProductsState.update { s -> s + cartChanges.keys }
            firstCartCopy to cartChanges
        }


        kotlin.runCatching {
            withTimeout(5000) {
                updateCartOnline(cartChanges, currentFirstCart)
            }
            updateCartListState(true)
        }.onFailure {
            cartMutex.withLock {
                val cartWithoutChanges = carts.keys.associateWith { key ->
                    val newValue = cartChanges[key] ?: return@associateWith carts[key] ?: 0
                    val oldValue = currentFirstCart[key] ?: 0
                    carts.getOrDefault(key, 0) - (newValue - oldValue)
                }
                updateCart(cartWithoutChanges)
            }
        }

        cartMutex.withLock {
            _blockedProductsState.update { it - cartChanges.keys }
        }
    }

    suspend fun add(
        id: Long,
        oldCount: Int,
        newCount: Int,
        withUpdate: Boolean = true,
    ) {
        val isInCart = oldCount != 0

        //val plus = newCount >= oldCount

        updateCarts(id, newCount)

        runCatching {
            action(id = id, count = newCount, isInCart = isInCart/*, plus*/)
            updateCartListState(withUpdate)
        }.onFailure {
            //tabManager.loadingAddToCart(false, plus = true)
            updateCarts(id, oldCount)
        }
    }

    suspend fun clearCart() = cartMutex.withLock {
        cartVersion++
        carts.clear()
        cartsStateListener.emit(carts)
        updateCartListState(true)
        tabManager.clearBottomNavCartState()
    }

    fun isCartEmpty() = carts.isEmpty()


    suspend fun syncCart(newCart: Map<Long, Int>) = cartMutex.withLock {
        if ((firstCart.isNotEmpty() && carts.isNotEmpty()) || blockedProductsState.value.isNotEmpty()) {
            return@withLock
        }
        updateCart(newCart)
        tabManager.saveBottomNavCartState()
    }

    suspend fun syncCart(list: List<ProductUI>) {
        list.forEach { product ->
            carts[product.id] = product.cartQuantity
        }
        tabManager.saveBottomNavCartState()
        cartsStateListener.emit(carts)
    }

    private suspend fun updateCartOnline(
        needUpdate: Map<Long, Int>,
        firstCart: Map<Long, Int>,
    ) = coroutineScope {
        if (needUpdate.isEmpty()) return@coroutineScope

        needUpdate.map { (productId, quantity) ->
            async {
                val exists = (firstCart[productId] ?: 0) > 0
                val call = if (exists) {
                    vodovozServiceRepository.updateProductInCart(productId, quantity)
                } else {
                    vodovozServiceRepository.addProductToCart(productId, quantity)
                }
                call.singleResult().getOrThrow()
            }
        }.awaitAll()
    }

    private suspend fun action(id: Long, count: Int, isInCart: Boolean/*, plus: Boolean*/) {
        if (!isInCart) {
            //tabManager.loadingAddToCart(true, plus = plus)
            repository.addProductToCart(id, count)

        } else {
            repository.changeProductsQuantityInCart(id, count)
            //tabManager.loadingAddToCart(true, plus = true)

        }
    }

    private suspend fun updateCart(cart: Map<Long, Int>) {
        carts.clear()
        carts.putAll(cart)
        cartsStateListener.emit(carts)
    }


    private suspend fun updateCarts(id: Long, count: Int) {
        carts[id] = count
        cartsStateListener.emit(carts)
    }

    suspend fun addProductWithGift(
        productId: String,
        giftId: String,
    ) = coroutineScope.launch {
        vodovozServiceRepository
            .addMultipleProductsToCart("$productId;$giftId")
            .singleResult()
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

    fun formatCart(cart: Map<Long, Int>): String {
        return cart.entries.joinToString(";") { "${it.key}-${it.value}" }
    }

}