package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.button.ProductQuantityWithCartButton
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonSmall
import com.vodovoz.app.design_system.composables.card.VodovozOutlinedCard
import com.vodovoz.app.design_system.composables.isElementVisible
import com.vodovoz.app.design_system.model.BlockPromoDataUi
import com.vodovoz.app.design_system.model.ButtonBlockUi
import com.vodovoz.app.design_system.model.ButtonDesignBlockUi
import com.vodovoz.app.design_system.model.BuyButtonUi
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.DesignBlockUi
import com.vodovoz.app.design_system.model.OldNewPriceUi
import com.vodovoz.app.design_system.model.ProductDetailsButtonsUi
import com.vodovoz.app.design_system.model.PromoProductUi

@Composable
fun ProductDetailsButtonsBlock(
    modifier: Modifier = Modifier,
    isAvailable: Boolean,
    quantityButtonIsLoading: Boolean,
    cartQuantity: Int,
    buttons: ProductDetailsButtonsUi,
    totalPrice: Int,
    onAddToCart: () -> Unit,
    onProductMinus: () -> Unit,
    onProductPlus: () -> Unit,
    onNavigateToCart: () -> Unit,
    onFloatingButtonChange: (Boolean) -> Unit,
    onMultiButtonClick: () -> Unit,
    onPresentButtonClick: () -> Unit,
    onPresentBlockButtonClick: () -> Unit,
    onPreOrderButtonClick: () -> Unit,
    onAnalogButtonClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {

        Spacer(Modifier.height(24.dp))

        val multiBuyButton = buttons.multiBuyButton

        if (multiBuyButton != null && isAvailable) {
            VodovozButton(
                text = multiBuyButton.name,
                onClick = onMultiButtonClick,
                modifier = Modifier,
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = multiBuyButton.textColor,
                    containerColor = multiBuyButton.backgroundColor
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        val presentButton = buttons.blockButton?.button

        if (presentButton != null && isAvailable) {
            VodovozButton(
                text = presentButton.name,
                onClick = onPresentButtonClick,
                modifier = Modifier,
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = presentButton.textColor,
                    containerColor = presentButton.backgroundColor
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }


        val analogButton = buttons.analogButton

        when {
            cartQuantity > 0 || quantityButtonIsLoading && isAvailable -> {
                ProductQuantityWithCartButton(
                    modifier = Modifier,
                    onProductMinus = onProductMinus,
                    onProductPlus = onProductPlus,
                    onCartClick = onNavigateToCart,
                    countProducts = cartQuantity,
                    currentPrice = totalPrice,
                    isLoading = quantityButtonIsLoading
                )
            }

            isAvailable -> {
                VodovozButton(
                    text = stringResource(R.string.to_cart),
                    onClick = onAddToCart,
                    modifier = Modifier.isElementVisible(onFloatingButtonChange),
                )
            }

            analogButton != null -> {
                VodovozButton(
                    text = analogButton.name,
                    onClick = onAnalogButtonClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = analogButton.textColor,
                        containerColor = analogButton.backgroundColor
                    ),
                    modifier = Modifier.isElementVisible(onFloatingButtonChange),

                )
            }
        }

        val preOrderButton = buttons.preOrderButton

        if (!isAvailable && preOrderButton != null) {
            VodovozButton(
                text = preOrderButton.name,
                onClick = onPreOrderButtonClick,
                modifier = Modifier
                    .padding(top = 16.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    contentColor = preOrderButton.textColor,
                    containerColor = preOrderButton.backgroundColor
                )
            )
        }

        val presentBlock = buttons.blockDesignButton
        if (presentBlock != null && isAvailable) {

            val blockInfo = presentBlock.block
            val blockInfoButton = blockInfo.button

            VodovozOutlinedCard(
                modifier = Modifier.padding(top = 24.dp),
                contentPadding = PaddingValues(16.dp),
                borderColor = blockInfo.borderColor,
                containerColor = blockInfo.background
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = blockInfo.image,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(50.dp),
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        text = blockInfo.title,
                        modifier = Modifier.padding(start = 16.dp),
                        color = blockInfo.textColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                VodovozButtonSmall(
                    modifier = Modifier.padding(top = 16.dp),
                    text = blockInfoButton.name,
                    onClick = onPresentBlockButtonClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blockInfoButton.backgroundColor,
                        contentColor = blockInfoButton.textColor
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProductDetailsButtonsBlockPreview() {
    VodovozTheme {
        ProductDetailsButtonsBlock(
            isAvailable = false,
            quantityButtonIsLoading = false,
            cartQuantity = 0,
            buttons = productDetailsButtonsUi.copy(multiBuyButton = null),
            totalPrice = 300,
            onAddToCart = { },
            onProductMinus = { },
            onProductPlus = { },
            onNavigateToCart = { },
            onFloatingButtonChange = {

            },
            onPresentButtonClick = {},
            onMultiButtonClick = {},
            onAnalogButtonClick = {},
            onPreOrderButtonClick = {},
            onPresentBlockButtonClick = {}
        )
    }
}


private val productDetailsButtonsUi = ProductDetailsButtonsUi(
    blockButton = ButtonBlockUi(
        button = ColorfulButtonUi(
            name = "Купить + Подарок",
            textColor = Color.White,
            backgroundColor = Color(0xFFFF5733) // Оранжево-красный
        ),
        data = BlockPromoDataUi(
            title = "Супер акция",
            description = "Скидка 20% на первую покупку!",
            productQuantityText = "1 шт.",
            product = PromoProductUi(
                name = "Смартфон X",
                image = "https://example.com/smartphone_x.png",
                price = OldNewPriceUi(
                    new = "25,990 ₽",
                    old = "32,990 ₽"
                )
            )
        ),
        buyButton = BuyButtonUi(
            textColor = Color.White,
            backgroundColor = Color(0xFFFF5733),
            title = "Купить сейчас",
            productId = "smartphone_x_001",
            moreProductId = "smartphone_x_more"
        )
    ),
    blockDesignButton = ButtonDesignBlockUi(
        block = DesignBlockUi(
            title = "Эксклюзивный дизайн",
            image = "https://example.com/design_button.png",
            background = Color.Black,
            textColor = Color.White,
            borderColor = Color(0xFFFFD700), // Золотой
            button = ColorfulButtonUi(
                name = "Купить дизайн",
                textColor = Color.Black,
                backgroundColor = Color(0xFFFFD700)
            )
        ),
        data = BlockPromoDataUi(
            title = "Дизайн-предложение",
            description = "Уникальный дизайн для вашего устройства",
            productQuantityText = "1 шт.",
            product = PromoProductUi(
                name = "Дизайнерский чехол",
                image = "https://example.com/case.png",
                price = OldNewPriceUi(
                    new = "1,990 ₽",
                    old = "2,490 ₽"
                )
            )
        ),
        buyButton = BuyButtonUi(
            textColor = Color.White,
            backgroundColor = Color.Black,
            title = "Заказать дизайн",
            productId = "case_001",
            moreProductId = "case_more"
        )
    ),
    multiBuyButton = ColorfulButtonUi(
        name = "Опт. покупка",
        textColor = Color.White,
        backgroundColor = Color(0xFF008000) // Зеленый
    ),
    analogButton = ColorfulButtonUi(
        name = "Аналоги",
        textColor = Color.White,
        backgroundColor = Color(0xFF0000FF) // Синий
    ),
    preOrderButton = ColorfulButtonUi(
        name = "Предзаказ",
        textColor = Color.Black,
        backgroundColor = Color(0xFFFFA500) // Оранжевый
    )
)
