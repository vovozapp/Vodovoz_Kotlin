package com.vodovoz.app.design_system.composables.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.button.QuantityButtonSmall
import com.vodovoz.app.design_system.composables.button.VodovozButtonSmall
import com.vodovoz.app.design_system.composables.chip.VodovozColorChipSmall
import com.vodovoz.app.feature.home.model.LabelWithColorUi
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.util.formatPrice
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LinearProductCard(
    modifier: Modifier = Modifier,
    product: ProductUi,
    onClick: (ProductUi) -> Unit,
    onLike: (ProductUi) -> Unit,
) {
    val percentLabels =
        product.labels.filter { labelEntity -> labelEntity.name.any { s -> s == '%' } }
    val otherLabels = product.labels - percentLabels.toSet()

    VodovozOutlinedCard(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        onClick = { onClick(product) }
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
            Box(
                modifier = Modifier
                    .height(132.dp)
                    .width(144.dp)
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Inside
                )

                Row(verticalAlignment = Alignment.Top) {
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

                    Spacer(modifier = Modifier.weight(1f))

                    percentLabels.forEach { label ->
                        VodovozColorChipSmall(color = label.color, text = label.name)
                    }
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                val labelSmall = MaterialTheme.typography.labelSmall
                Text(
                    text = product.name,
                    modifier = Modifier.height(48.dp),
                    maxLines = 3,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = labelSmall.copy(fontSize = (labelSmall.fontSize.value - 1).sp)
                )

                Row(
                    modifier = Modifier
                        .padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {

                        Text(
                            modifier = Modifier.alignByBaseline(),
                            text = stringResource(
                                R.string.price,
                                product.price.roundToInt().formatPrice()
                            ),
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


                val pricePerUnitText =
                    if (product.pricePerUnit != null && product.unitOfMeasurement != null) stringResource(
                        R.string.unit_of_measurement,
                        product.pricePerUnit ?: 0,
                        product.unitOfMeasurement ?: ""
                    )
                    else ""

                Text(
                    modifier = Modifier.width(10.dp),
                    maxLines = 1,
                    text = pricePerUnitText,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = ExtendedTheme.typography.labelExtraSmallVariant
                )

                Spacer(modifier = Modifier.weight(1f))

                val buttonIsLoading = product.cartLoading
                if (buttonIsLoading || product.cartQuantity > 0) {
                    QuantityButtonSmall(
                        isLoading = buttonIsLoading,
                        quantity = product.cartQuantity,
                        onPlus = { },
                        onMinus = { }
                    )
                } else {
                    VodovozButtonSmall(
                        text = stringResource(id = R.string.to_cart),
                        onClick = { onClick(product) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LinearProductCardPreview() {
    VodovozTheme {
        val sampleProduct = ProductUi(
            id = 101L,
            isFavorite = true,
            rating = 4.5f,
            price = 2900009.99f,
            oldPrice = 349000000230.99f,
            name = "Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21Смартфон Galaxy S21",
            cartQuantity = 1,
            cartLoading = false,
            image = "https://vodovoz.net/upload/iblock/9ed/ec5cfujet9sztz077mtdzofrzjqzn0zj.jpeg",
            labels = listOf(
                LabelWithColorUi("Новинка", Color.Red),
                LabelWithColorUi("Хит продаж", Color.Green)
            ),
            isAvailable = true,
            pricePerUnit = null,
            unitOfMeasurement = null
        )


        LinearProductCard(product = sampleProduct, onClick = {}) {

        }
    }
}