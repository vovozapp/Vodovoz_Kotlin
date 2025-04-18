package com.vodovoz.app.domain.general.respository

import com.vodovoz.app.domain.general.model.CartBatchOperation
import com.vodovoz.app.domain.general.model.CartManagerItemModel
import com.vodovoz.app.domain.general.model.CartOperation

interface CartManagerRepository {

    suspend fun getCartItems(): List<CartManagerItemModel>

    suspend fun getCartVersion(): Long

    suspend fun addItems(
        items: List<CartManagerItemModel>,
        updateVersion: Boolean = true,
    ): CartBatchOperation

    suspend fun replaceItems(
        items: List<CartManagerItemModel>,
        updateVersion: Boolean = true,
    ): CartBatchOperation

    suspend fun clearCart(updateVersion: Boolean = true): CartBatchOperation

    suspend fun removeItem(
        productId: Long,
        updateVersion: Boolean = true,
    ): CartOperation

    suspend fun decrementItemQuantity(
        productId: Long,
        updateVersion: Boolean = true,
    ): CartOperation

    suspend fun incrementItemQuantity(
        productId: Long,
        updateVersion: Boolean = true,
    ): CartOperation


}