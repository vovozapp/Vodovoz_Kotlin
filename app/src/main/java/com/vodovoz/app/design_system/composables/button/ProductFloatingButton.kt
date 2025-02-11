package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.buildPriceAndOldPriceAnnotatedString
import com.vodovoz.app.design_system.model.ColorfulButtonUi

@Composable
fun ProductFloatingButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    cartQuantity: Int,
    totalPrice: Int,
    oldPrice: Int,
    price: Int,
    leftToGift: Int,
    analogButton: ColorfulButtonUi?,
    isAvailable: Boolean,
    onProductPlus: () -> Unit,
    onProductMinus: () -> Unit,
    onCartClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    onAnalogClick: () -> Unit,
) {
    val buttonShape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, buttonShape)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = buttonShape
            )
            .padding(top = 10.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(isAvailable) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 11.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(
                        R.string.left_to_gift, leftToGift
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }


        when {
            cartQuantity > 0 || isLoading && isAvailable -> {
                ProductQuantityWithCartButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onProductMinus = onProductMinus,
                    onProductPlus = onProductPlus,
                    onCartClick = onCartClick,
                    countProducts = cartQuantity,
                    currentPrice = totalPrice,
                    isLoading = isLoading
                )
            }

            isAvailable -> {
                VodovozButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = buildPriceAndOldPriceAnnotatedString(
                        price = price,
                        oldPrice = oldPrice
                    ),
                    onClick = onAddToCartClick
                )
            }

            analogButton != null -> {
                VodovozButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = analogButton.name,
                    onClick = onAnalogClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        contentColor = analogButton.textColor,
                        containerColor = analogButton.backgroundColor
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun FloatingProductButtonPreview() {
    VodovozTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.BottomCenter
        ) {
            ProductFloatingButton(
                isLoading = false,
                cartQuantity = 0,
                totalPrice = 500,
                oldPrice = 300,
                price = 250,
                leftToGift = 500,
                onProductPlus = {},
                onProductMinus = {},
                onCartClick = {},
                onAddToCartClick = {},
                isAvailable = false,
                analogButton = ColorfulButtonUi("Analog", Color.Black, Color.White),
                onAnalogClick = {}
            )
        }
    }
}