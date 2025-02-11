package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.model.DepositUi
import com.vodovoz.app.design_system.model.PriceUi
import com.vodovoz.app.util.formatPrice
import kotlin.math.roundToInt

@Composable
fun ProductDetailsPriceInfo(
    modifier: Modifier = Modifier,
    firstPrice: PriceUi,
    deposit: DepositUi?,
    pricePerUnit: String,
) {
    val price = firstPrice.price
    val oldPrice = firstPrice.oldPrice

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = stringResource(R.string.price, price.roundToInt().formatPrice()),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )

            if (oldPrice > price) {
                Text(
                    text = stringResource(R.string.price, oldPrice.roundToInt().formatPrice()),
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.labelLarge.copy(
                        textDecoration = TextDecoration.LineThrough,
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (pricePerUnit.isNotEmpty()) {
            Text(
                text = pricePerUnit,
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (deposit?.price != null && deposit.price > 0f) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = buildAnnotatedDepositString(deposit = deposit.price.roundToInt()))
                Icon(
                    painter = painterResource(id = R.drawable.ic_question_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

}

@Composable
private fun buildAnnotatedDepositString(deposit: Int): AnnotatedString {

    val bodySmall =
        MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onBackground)
    val startText = stringResource(id = R.string.deposit_start_text)
    val priceText = stringResource(id = R.string.price_text, deposit)
    val endText = stringResource(id = R.string.deposit_end_text)

    return buildAnnotatedString {
        withStyle(bodySmall.toSpanStyle()) {
            append(startText)
        }
        withStyle(bodySmall.copy(fontWeight = FontWeight.Medium).toSpanStyle()) {
            append(priceText)
        }
        withStyle(bodySmall.toSpanStyle()) {
            append(endText)
        }
    }
}