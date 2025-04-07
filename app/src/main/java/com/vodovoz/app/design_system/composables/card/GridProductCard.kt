package com.vodovoz.app.design_system.composables.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.button.QuantityButtonSmall
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults
import com.vodovoz.app.design_system.composables.button.VodovozButtonSmall
import com.vodovoz.app.design_system.composables.chip.VodovozColorChipSmall
import com.vodovoz.app.design_system.model.LabelWithColorUi
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.util.formatPrice
import java.util.Locale
import kotlin.math.roundToInt


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GridProductCard(
    modifier: Modifier = Modifier,
    product: ProductUi,
    onClick: (ProductUi) -> Unit,
    onLike: (ProductUi) -> Unit,
    onAnalogsClick: (ProductUi) -> Unit = {},
    onIncrementToCart: (ProductUi) -> Unit = {},
    onDecrementToCart: (ProductUi) -> Unit = {}
) {
    val percentLabels =
        product.labels.filter { labelEntity -> labelEntity.name.any { s -> s == '%' } }
    val otherLabels = product.labels - percentLabels.toSet()

    VodovozOutlinedCard(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        onClick = { onClick(product) }
    ) {
        Box(modifier = Modifier) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .height(105.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Inside
            )
            Row {
                percentLabels.forEach { label ->
                    VodovozColorChipSmall(color = label.color, text = label.name)
                }
                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = if (product.isFavorite) R.drawable.ic_filled_like else R.drawable.ic_like),
                    contentDescription = null,
                    tint = if (product.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable(
                            onClick = { onLike(product) },
                            indication = null,
                            interactionSource = null
                        )
                )
            }

            FlowRow(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                otherLabels.forEach { label ->
                    VodovozColorChipSmall(color = label.color, text = label.name)
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 4.dp)
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom, modifier = Modifier) {

                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = stringResource(R.string.price, product.price.roundToInt().formatPrice()),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1
                )

                if (product.oldPrice > product.price) {
                    Text(
                        text = stringResource(
                            R.string.price,
                            product.oldPrice.roundToInt().formatPrice()
                        ),
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = ExtendedTheme.typography.labelExtraSmallVariant.copy(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        modifier = Modifier
                            .alignByBaseline()
                            .padding(start = 8.dp)
                            .weight(1f, false),
                        maxLines = 1,
                    )
                }
            }


            Spacer(modifier = Modifier.weight(1f))


            Icon(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                tint = if (product.rating <= 0.0f) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(18.dp),
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 2.dp),
                text = if (product.rating > 0) String.format(
                    Locale.getDefault(),
                    "%.1f",
                    product.rating
                ) else 0.toString(),
                color = if (product.rating <= 0.0f) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.onBackground,
                style = ExtendedTheme.typography.labelMediumVariant,
                maxLines = 1
            )


        }

        val labelSmall = MaterialTheme.typography.labelSmall
        Text(
            text = product.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = labelSmall.copy(fontSize = (labelSmall.fontSize.value - 1).sp),
            modifier = Modifier
                .padding(top = 12.dp)
                .height(48.dp),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )


        val buttonIsLoading = product.cartLoading

        Spacer(modifier = Modifier.height(8.dp))

        when {
            !product.isAvailable -> {
                VodovozButtonSmall(
                    text = stringResource(id = R.string.analogs),
                    onClick = { onAnalogsClick(product) },
                    colors = VodovozButtonDefaults.secondaryColors()
                )
            }

            product.cartQuantity > 0 -> {
                QuantityButtonSmall(
                    isLoading = buttonIsLoading,
                    quantity = product.cartQuantity,
                    onPlus = { onIncrementToCart(product) },
                    onMinus = { onDecrementToCart(product) }
                )
            }

            else -> {
                VodovozButtonSmall(
                    text = stringResource(id = R.string.to_cart),
                    onClick = { onIncrementToCart(product) },
                )
            }
        }
    }
}


@Preview
@Composable
private fun GridProductCardPreview() {
    VodovozTheme {
        val sampleProduct = ProductUi(
            id = 101L,
            isFavorite = true,
            rating = 4.5f,
            price = 2900009.99f,
            oldPrice = 349000000230.99f,
            name = "Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21",
            cartQuantity = 0,
            cartLoading = false,
            image = "https://vodovoz.net/upload/iblock/9ed/ec5cfujet9sztz077mtdzofrzjqzn0zj.jpeg",
            labels = listOf(
                LabelWithColorUi("Новинка", Color.Red),
                LabelWithColorUi("Хит продаж", Color.Green)
            ),
            isAvailable = false,
            pricePerUnit = null,
            unitOfMeasurement = null
        )

        GridProductCard(product = sampleProduct, onClick = {}, modifier = Modifier, onLike = {}) {

        }
    }
}



