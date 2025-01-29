package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.util.formatPrice

@Composable
fun ProductQuantityButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    quantity: Int,
    totalPrice: Int,
) {
    val priceAnnotatedString = buildPriceAnnotatedString(quantity, totalPrice)

    Surface(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.clickable {
                if (!isLoading) {
                    onMinus()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_minus),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.background
                )
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.background,
                        trackColor = Color.Transparent,
                        strokeWidth = 1.dp
                    )
                } else {
                    Text(text = priceAnnotatedString, color = MaterialTheme.colorScheme.background)
                }
            }

            Box(Modifier.clickable {
                if (!isLoading) {
                    onPlus()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}


@Composable
private fun buildPriceAnnotatedString(count: Int, currentPrice: Int): AnnotatedString {

    val priceWithCurrency = stringResource(id = R.string.price, currentPrice.formatPrice())
    val countWithX = stringResource(id = R.string.count_with_x, count)

    val bodyMedium = MaterialTheme.typography.bodyMedium
    val headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)

    return buildAnnotatedString {
        withStyle(bodyMedium.toSpanStyle()) {
            append(countWithX)
        }
        withStyle(headlineSmall.toSpanStyle()) {
            append(priceWithCurrency)
        }
    }
}

@Preview
@Composable
private fun CartButtonPreview() {
    VodovozTheme {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            CartButton(count = 10) {

            }
        }
    }
}



