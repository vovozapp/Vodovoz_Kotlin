package com.vodovoz.app.data.local_cart_database.repository

import com.vodovoz.app.data.local_cart_database.CartDao
import com.vodovoz.app.data.local_cart_database.mappers.mapToData
import com.vodovoz.app.data.local_cart_database.mappers.mapToDomain
import com.vodovoz.app.data.local_cart_database.model.CartItemEntity
import com.vodovoz.app.domain.general.model.CartBatchOperation
import com.vodovoz.app.domain.general.model.CartManagerItemModel
import com.vodovoz.app.domain.general.model.CartOperation
import com.vodovoz.app.domain.general.respository.CartManagerRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartManagerRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
) : CartManagerRepository {

    private val mutex = Mutex()


    override suspend fun getCartItems(): List<CartManagerItemModel> {
        return cartDao.getCarItems().first().mapToDomain()
    }

    override suspend fun getCartVersion(): Long {
        return cartDao.getCart().version
    }

    override suspend fun addItems(
        items: List<CartManagerItemModel>,
        updateVersion: Boolean,
    ): CartBatchOperation = mutex.withLock {
        val newItems = items.mapToData()
        cartDao.insertCartItems(newItems)
        CartBatchOperation(
            cartVersion = if (updateVersion) cartDao.updateVersion() else -1L,
            items = getCartItems()
        )
    }

    override suspend fun replaceItems(
        items: List<CartManagerItemModel>,
        updateVersion: Boolean,
    ): CartBatchOperation = mutex.withLock {
        val newItems = items.mapToData()
        cartDao.clearCart()
        cartDao.insertCartItems(newItems)
        CartBatchOperation(cartVersion = if (updateVersion) cartDao.updateVersion() else -1L, items)
    }

    override suspend fun clearCart(updateVersion: Boolean): CartBatchOperation =
        mutex.withLock {
            cartDao.clearCart()
            CartBatchOperation(
                cartVersion = if (updateVersion) cartDao.updateVersion() else -1L,
                items = emptyList(),
            )
        }

    override suspend fun removeItem(
        productId: Long,
        updateVersion: Boolean,
    ): CartOperation = mutex.withLock {
        cartDao.deleteCartItem(productId)

        CartOperation(
            cartVersion = if (updateVersion) cartDao.updateVersion() else -1L,
            productId = productId,
            newQuantity = 0
        )
    }

    override suspend fun decrementItemQuantity(
        productId: Long,
        updateVersion: Boolean,
    ): CartOperation = mutex.withLock {
        val currentItem = cartDao.getCartItemById(productId)
        val newQuantity = currentItem.quantity - 1
        if (newQuantity > 0) cartDao.insertCartItem(currentItem.copy(quantity = newQuantity)) else cartDao.deleteCartItem(
            productId
        )

        CartOperation(
            cartVersion = if (updateVersion) cartDao.updateVersion() else -1L,
            productId = productId,
            newQuantity = newQuantity
        )
    }


    override suspend fun incrementItemQuantity(
        productId: Long,
        updateVersion: Boolean,
    ): CartOperation = mutex.withLock {
        val currentItem =
            runCatching { cartDao.getCartItemById(productId) }.getOrNull() ?: CartItemEntity(
                productId,
                0
            )
        val newQuantity = currentItem.quantity + 1
        cartDao.insertCartItem(currentItem.copy(quantity = newQuantity))

        CartOperation(
            cartVersion = if (updateVersion) cartDao.updateVersion() else -1L,
            productId = productId,
            newQuantity = newQuantity
        )
    }


}