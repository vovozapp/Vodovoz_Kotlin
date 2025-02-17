package com.vodovoz.app.data.local_cart_database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val id: Int = 0,
    val version: Long
)

data class CartWithItems(
    @Embedded val cart: CartEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "cartId"
    )
    val items: List<CartItemEntity>
)