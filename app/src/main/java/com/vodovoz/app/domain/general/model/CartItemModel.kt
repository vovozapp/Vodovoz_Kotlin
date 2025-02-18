package com.vodovoz.app.domain.general.model

data class CartItemModel(
    val productId: Long,
    val quantity: Int,
)

fun parseCartString(cartString: String): List<CartItemModel> {
    return cartString.split(";")
        .mapNotNull {
            val parts = it.split("-")
            if (parts.size == 2) {
                val productId = parts[0].toLongOrNull()
                val quantity = parts[1].toIntOrNull()
                if (productId != null && quantity != null) {
                    CartItemModel(productId, quantity)
                } else null
            } else null
        }
}

fun formatCartItems(cartItems: List<CartItemModel>): String {
    return cartItems.joinToString(";") { "${it.productId}-${it.quantity}" }
}

data class CartModel(
    val id: Int,
    val version: Long,
)

data class CartOperation(
    val cartVersion: Long,
    val productId: Long,
    val newQuantity: Int,
)

data class CartBatchOperation(
    val cartVersion: Long,
    val items: List<CartItemModel>
)