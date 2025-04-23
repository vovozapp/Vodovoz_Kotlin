package com.vodovoz.app.design_system.composables.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.model.OrderProductUi
import com.vodovoz.app.feature.cart.model.ProductRestrictionUi

@Composable
fun OrderProductCard(
    modifier: Modifier = Modifier,
    product: OrderProductUi,
    onClick: (OrderProductUi) -> Unit,
    onLikeClick: (OrderProductUi) -> Unit,
) {
    val restrictions = product.restrictions

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .clickable(
                onClick = {
                    if (product.isShowcaseProduct) {
                        onClick(product)
                    }
                },
                interactionSource = null,
                indication = null
            )
    ) {
        Box{
            Image(
                painter = rememberAsyncImagePainter(model = product.image),
                contentDescription = null,
                modifier = Modifier
                    .size(76.dp)
                    .alpha(if (product.catalogQuantity < 1) 0.5f else 1f)
            )
            //TODO - mb put labels
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = product.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )

                if (restrictions != ProductRestrictionUi.NO_FAVORITES && restrictions != ProductRestrictionUi.FULL_RESTRICTION && restrictions != ProductRestrictionUi.NO_FAVORITES_QUANTITY) {
                    Icon(
                        painter = painterResource(id = if (product.isFavorite) R.drawable.ic_filled_like else R.drawable.ic_like),
                        contentDescription = null,
                        tint = if (product.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceTint,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(18.dp)
                            .clickable(
                                onClick = { onLikeClick(product) },
                                indication = null,
                                interactionSource = null
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val presentLabel = product.present
            if (presentLabel == null) {
                Text(
                    text = product.articleNumberText,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = ExtendedTheme.typography.labelSmallVariant
                )

                product.depositText?.let { depositText ->
                    Text(
                        text = depositText,
                        color = MaterialTheme.colorScheme.surfaceTint,
                        style = ExtendedTheme.typography.labelSmallVariant
                    )
                }

                if (product.catalogQuantity < 1) {
                    Text(
                        text = stringResource(R.string.product_end),
                        color = MaterialTheme.colorScheme.error,
                        style = ExtendedTheme.typography.labelSmallVariant
                    )
                }

            } else {
                Text(
                    text = presentLabel.title,
                    color = presentLabel.color,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            val price = product.price
            if (price != null && product.catalogQuantity > 0) {
                Row(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = stringResource(id = R.string.price, price.price.toInt()),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    if (price.price < price.oldPrice) {
                        Text(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .alignByBaseline(),
                            text = stringResource(R.string.price, price.oldPrice.toInt()),
                            color = MaterialTheme.colorScheme.surfaceTint,
                            style = ExtendedTheme.typography.labelExtraSmallVariant.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                        )
                    }
                }
            }


        }

    }
}