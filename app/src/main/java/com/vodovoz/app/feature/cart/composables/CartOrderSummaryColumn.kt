package com.vodovoz.app.feature.cart.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.domain.general.model.cart.CartOrderSummaryUi

@Composable
fun CartOrderSummaryColumn(modifier: Modifier = Modifier, orderSummary: CartOrderSummaryUi) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.order_total),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                text = orderSummary.finalPriceText,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End
            )
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (orderSummary.productsPriceText.isNotEmpty()) {
            OrderSummaryItem(
                name = stringResource(R.string.products_of_sum),
                value = orderSummary.productsPriceText
            )
        }

        if (orderSummary.depositText.isNotEmpty()) {
            OrderSummaryItem(
                name = stringResource(id = R.string.order_deposit),
                value = orderSummary.depositText
            )
        }
        if (orderSummary.presentText.isNotEmpty()) {
            OrderSummaryItem(
                name = stringResource(id = R.string.order_gift),
                value = orderSummary.presentText
            )
        }

        if (orderSummary.discountText.isNotEmpty()) {
            OrderSummaryItem(
                name = stringResource(id = R.string.order_discount),
                value = orderSummary.discountText,
                color = MaterialTheme.colorScheme.secondary
            )
        }

    }
}

@Composable
private fun OrderSummaryItem(
    modifier: Modifier = Modifier,
    name: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.surfaceTint,
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.weight(1f),
            text = name,
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            text = value,
            color = color,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End
        )

    }
}