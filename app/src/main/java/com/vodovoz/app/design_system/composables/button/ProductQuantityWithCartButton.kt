package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductQuantityWithCartButton(
    modifier: Modifier = Modifier,
    onProductPlus: () -> Unit,
    onProductMinus: () -> Unit,
    onCartClick: () -> Unit,
    countProducts: Int,
    currentPrice: Int,
    isLoading: Boolean
) {
    Row(modifier = modifier.padding(horizontal = 16.dp)) {
        ProductQuantityButton(
            modifier = Modifier.weight(1f),
            isLoading = isLoading,
            onPlus = onProductPlus,
            onMinus = onProductMinus,
            quantity = countProducts,
            totalPrice = currentPrice
        )
        CartButton(
            modifier = Modifier.padding(start = 8.dp),
            count = countProducts,
            onClick = onCartClick
        )
    }
}