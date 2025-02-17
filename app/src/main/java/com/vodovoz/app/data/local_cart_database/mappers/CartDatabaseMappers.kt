package com.vodovoz.app.data.local_cart_database.mappers

import com.vodovoz.app.data.local_cart_database.model.CartEntity
import com.vodovoz.app.data.local_cart_database.model.CartItemEntity
import com.vodovoz.app.domain.general.model.CartItemModel
import com.vodovoz.app.domain.general.model.CartModel

fun CartItemModel.toData(): CartItemEntity {
    return CartItemEntity(
        productId = productId,
        quantity = quantity
    )
}

fun List<CartItemModel>.mapToData(): List<CartItemEntity> {
    return map { it.toData() }
}

fun CartItemEntity.toDomain(): CartItemModel {
    return CartItemModel(
        productId = productId,
        quantity = quantity
    )
}

@JvmName("CartItemEntityToCartItemModel")
fun List<CartItemEntity>.mapToDomain(): List<CartItemModel> {
    return map { it.toDomain() }
}

fun CartEntity.toDomain(): CartModel {
    return CartModel(
        version = version,
        id = 0
    )
}

fun CartModel.toData(): CartEntity {
    return CartEntity(
        id = 0,
        version = version
    )
}
