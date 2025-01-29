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
import com.vodovoz.app.ui.model.PriceUI
import com.vodovoz.app.util.formatPrice
import kotlin.math.roundToInt

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsPriceInfo(
    modifier: Modifier = Modifier,
    priceUIList: List<PriceUI>,
    deposit: Int,
) {
    val (currentPrice, oldPrice) = priceUIList.firstOrNull()?.run {
        currentPrice to oldPrice
    } ?: (0.0 to 0.0)

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = stringResource(R.string.price, currentPrice.roundToInt().formatPrice()),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )

            if (oldPrice > currentPrice) {
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
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //todo - put actual deposit
            Text(text = buildAnnotatedDepositString(deposit = deposit))
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