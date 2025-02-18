package com.vodovoz.app.data.local_cart_database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.vodovoz.app.data.local_cart_database.model.CartEntity
import com.vodovoz.app.data.local_cart_database.model.CartItemEntity
import com.vodovoz.app.data.local_cart_database.model.CartWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {


    @Transaction
    @Query("SELECT * FROM cart LIMIT 1")
    suspend fun getCart(): CartEntity

    @Transaction
    @Query("SELECT * FROM cart_item WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemById(productId: Long): CartItemEntity

    @Transaction
    @Query("SELECT * FROM cart WHERE id = 0")
    suspend fun getCartWithItems(): CartWithItems

    @Transaction
    @Query("SELECT * FROM cart_item WHERE cartId = 0")
    fun getCarItems(): Flow<List<CartItemEntity>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartItemEntity>)

    @Transaction
    @Query("DELETE FROM cart_item WHERE productId = :productId")
    suspend fun deleteCartItem(productId: Long)

    @Transaction
    @Query("DELETE FROM cart_item WHERE cartId = 0")
    suspend fun clearCart()

    @Transaction
    suspend fun updateVersion(): Long {
        val newVersion = System.currentTimeMillis()
        insertCart(CartEntity(id = 0, version = newVersion))
        return newVersion
    }
}
