package com.vodovoz.app.data.local_cart_database.repository

import com.vodovoz.app.data.local_cart_database.CartDao
import com.vodovoz.app.data.local_cart_database.mappers.mapToData
import com.vodovoz.app.data.local_cart_database.mappers.mapToDomain
import com.vodovoz.app.data.local_cart_database.mappers.toData
import com.vodovoz.app.data.local_cart_database.mappers.toDomain
import com.vodovoz.app.domain.general.model.CartItemModel
import com.vodovoz.app.domain.general.model.CartModel
import com.vodovoz.app.domain.general.respository.CartDatabaseRepository

class CartDatabaseRepositoryImpl(
    private val cartDao: CartDao,
) : CartDatabaseRepository {

    override suspend fun getCartItemById(productId: Long): Result<CartItemModel> {
        return runCatching {
            cartDao.getCartItemById(productId).toDomain()
        }
    }

    override suspend fun replaceCartItems(items: List<CartItemModel>): Result<Boolean> =
        runCatching {
            cartDao.replaceCartItems(items.mapToData())
            true
        }

    override suspend fun addItemToCart(item: CartItemModel): Result<Boolean> = runCatching {
        cartDao.insertCartItem(item.toData())
        true
    }

    override suspend fun addItemsToCart(items: List<CartItemModel>): Result<Boolean> = runCatching {
        cartDao.insertCartItems(items.mapToData())
        true
    }

    override suspend fun removeItemForCart(item: CartItemModel): Result<Boolean> = runCatching {
        cartDao.deleteCartItem(item.productId)
        true
    }


    override suspend fun clearCart(): Result<Boolean> = runCatching {
        cartDao.clearCartItems()
        true
    }

    override suspend fun getCart(): Result<Pair<CartModel, List<CartItemModel>>> =
        runCatching {
            with(cartDao.getCartWithItems()) {
                cart.toDomain() to items.mapToDomain()
            }
        }

    override suspend fun updateVersion(): Result<Boolean> {
        return runCatching {
            cartDao.updateVersion()
            true
        }
    }


}