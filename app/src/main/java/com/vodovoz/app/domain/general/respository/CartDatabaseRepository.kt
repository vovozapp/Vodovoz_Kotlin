package com.vodovoz.app.domain.general.respository

import com.vodovoz.app.domain.general.model.CartItemModel
import com.vodovoz.app.domain.general.model.CartModel

interface CartDatabaseRepository {

    suspend fun getCartItemById(productId: Long): Result<CartItemModel>

    suspend fun replaceCartItems(items: List<CartItemModel>): Result<Boolean>

    suspend fun addItemToCart(item: CartItemModel): Result<Boolean>

    suspend fun addItemsToCart(items: List<CartItemModel>): Result<Boolean>

    suspend fun removeItemForCart(item: CartItemModel): Result<Boolean>

    suspend fun clearCart(): Result<Boolean>

    suspend fun getCart(): Result<Pair<CartModel, List<CartItemModel>>>

    suspend fun updateVersion(): Result<Boolean>


}