package com.vodovoz.app.design_system.composables.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.vodovoz.app.design_system.composables.ClickableIcon
import com.vodovoz.app.design_system.composables.button.VodovozButtonSmall
import com.vodovoz.app.ui.model.PriceUI
import com.vodovoz.app.ui.model.ProductUI
import com.vodovoz.app.util.formatPrice
import com.vodovoz.app.util.fromHexOrTransparent
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GridProductCard(
    modifier: Modifier = Modifier,
    product: ProductUI,
    onClick: (ProductUI) -> Unit,
    onLike: (ProductUI) -> Unit,
) {
    val percentLabels =
        product.labels.filter { labelEntity -> labelEntity.name.any { s -> s == '%' } }
    val labels = product.labels - percentLabels.toSet()

    VodovozOutlinedCard(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        onClick = { onClick(product) }
    ) {
        Box {
            AsyncImage(
                model = product.detailPicture,
                contentDescription = null,
                modifier = Modifier
                    .height(105.dp)
                    .fillMaxWidth()
            )
            Row {
                percentLabels.forEach { label ->
                    Surface(
                        contentColor = MaterialTheme.colorScheme.background,
                        color = Color.fromHexOrTransparent(label.color),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            text = label.name,
                            style = ExtendedTheme.typography.labelExtraSmall
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                ClickableIcon(
                    painter = painterResource(id = if (product.isFavorite) R.drawable.ic_filled_like else R.drawable.ic_like),
                    modifier = Modifier.size(18.dp),
                    onClick = { onLike(product) },
                    tint = if (product.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint
                )
            }

            FlowRow(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                labels.forEach { label ->
                    Surface(
                        contentColor = MaterialTheme.colorScheme.background,
                        color = Color.fromHexOrTransparent(label.color),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            text = label.name,
                            style = ExtendedTheme.typography.labelExtraSmall
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                val (currentPrice, oldPrice) = product.priceList.firstOrNull()?.run {
                    currentPrice to oldPrice
                } ?: (0.0 to 0.0)

                Text(
                    modifier = Modifier.alignByBaseline(),
                    text = stringResource(R.string.price, currentPrice.roundToInt().formatPrice()),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                )

                if (oldPrice > currentPrice) {
                    Text(
                        text = stringResource(R.string.price, oldPrice.roundToInt().formatPrice()),
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = ExtendedTheme.typography.labelExtraSmallVariant.copy(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        modifier = Modifier
                            .alignByBaseline()
                            .padding(start = 8.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                tint = if (product.rating <= 0.0f) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier
                    .padding(start = 2.dp)
                    .size(18.dp),
            )

            Text(
                modifier = Modifier.padding(start = 2.dp),
                text = if (product.rating > 0) String.format(
                    Locale.getDefault(),
                    "%.1f",
                    product.rating
                ) else 0.toString(),
                color = if (product.rating <= 0.0f) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.onBackground,
                style = ExtendedTheme.typography.labelMediumVariant
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


        VodovozButtonSmall(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = R.string.to_cart),
            onClick = { onClick(product) },
        )


    }
}

@Preview
@Composable
private fun GridProductCardPreview() {
    VodovozTheme {
        val productUI = ProductUI(
            id = 123456L,
            name = "Кристально чистая артезианская вода из экологически чистого региона с добавлением натуральных минералов для поддержания здоровья и жизненной энергии",
            detailPicture = "https://http.cat/images/101.jpg",
            isFavorite = true,
            leftItems = 10,
            pricePerUnit = "100.00",
            priceList = listOf(
                PriceUI(currentPrice = 100.0, oldPrice = 150.0, 0, 10)
            ),
            status = "В наличии",
            statusColor = "green",
            labels = listOf(),
            rating = 4.5f,
            isBottle = false,
            isGift = false,
            isAvailable = true,
            canBuy = true,
            commentAmount = "100 комментариев",
            cartQuantity = 2,
            orderQuantity = 1,
            depositPrice = 50,
            replacementProductUIList = listOf(),
            oldQuantity = 5,
            linear = true,
            currentPriceStringBuilder = "100.00",
            oldPriceStringBuilder = "120.00",
            minimalPriceStringBuilder = "80.00",
            haveDiscount = true,
            priceConditionStringBuilder = "Акция: 20% скидка",
            discountPercentStringBuilder = "20%",
            serviceDetailCoef = null,
            serviceGiftId = "gift123",
            chipsBan = null,
            totalDisc = 20.0,
            conditionPrice = "100.00",
            condition = "Обычные условия",
            forCart = true,
            giftText = "Подарок при заказе"
        )


        GridProductCard(product = productUI, onClick = {}, modifier = Modifier.width(200.dp)) {

        }
    }
}



