package com.vodovoz.app.feature.all.orders.detail.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.card.OrderProductCard
import com.vodovoz.app.design_system.model.LabelUi
import com.vodovoz.app.design_system.model.OrderProductPresentUi
import com.vodovoz.app.design_system.model.OrderProductUi
import com.vodovoz.app.design_system.model.PriceUi
import com.vodovoz.app.feature.cart.model.ProductRestrictionUi

@Suppress("NonSkippableComposable")
@Composable
fun OrderDetailsProductColumn(
    modifier: Modifier = Modifier,
    title: String,
    products: List<OrderProductUi>,
    onProductClick: (OrderProductUi) -> Unit,
    onProductLike: (OrderProductUi) -> Unit,
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        products.forEachIndexed { index, product ->
            key(product.name + product.id) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OrderProductCard(
                        product = product,
                        onClick = onProductClick,
                        onLikeClick = onProductLike
                    )

                    if (index != products.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    apiLevel = 34
)
@Composable
fun OrderDetailsProductColumnPreview() {
    VodovozTheme {
        val sampleProducts = listOf(
            OrderProductUi(
                id = 1L,
                name = "Product 1",
                quantity = 2,
                articleNumberText = "Art.12345",
                depositText = "10₽",
                price = PriceUi(100f, 0f, 1, 1),
                isShowcaseProduct = false,
                image = "",
                labels = listOf(LabelUi("Label", Color.Black, Color.White)),
                pricePerUnit = 50,
                unitOfMeasurement = "шт.",
                catalogQuantity = 10,
                isFavorite = false,
                present = OrderProductPresentUi("Подарок", Color.Green),
                restrictions = ProductRestrictionUi.NO_FAVORITES_QUANTITY,

                ),
            OrderProductUi(
                id = 2L,
                name = "Product 2",
                quantity = 1,
                articleNumberText = "Art.67890",
                depositText = null,
                price = PriceUi(100f, 300f, 1, 1),
                isShowcaseProduct = true,
                image = "",
                labels = emptyList(),
                pricePerUnit = null,
                unitOfMeasurement = null,
                catalogQuantity = 3,
                isFavorite = true,
                present = null,
                restrictions = ProductRestrictionUi.NO_FAVORITES
            ),
            OrderProductUi(
                id = 2L,
                name = "Product 2",
                quantity = 1,
                articleNumberText = "Art.67890",
                depositText = null,
                price = PriceUi(100f, 300f, 1, 1),
                isShowcaseProduct = true,
                image = "",
                labels = emptyList(),
                pricePerUnit = null,
                unitOfMeasurement = null,
                catalogQuantity = -1,
                isFavorite = true,
                present = null,
                restrictions = ProductRestrictionUi.NONE
            )
        )

        OrderDetailsProductColumn(
            title = "Products",
            products = sampleProducts,
            onProductClick = {},
            onProductLike = {}
        )
    }
}