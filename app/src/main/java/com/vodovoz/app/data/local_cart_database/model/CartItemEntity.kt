package com.vodovoz.app.data.local_cart_database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_item",
    foreignKeys = [
        ForeignKey(
            entity = CartEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cartId"])]
)

data class CartItemEntity(
    @PrimaryKey val productId: Long,
    val quantity: Int,
    val cartId: Int = 0
)
