package com.vodovoz.app.design_system.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme


@Composable
fun buildPriceAndOldPriceAnnotatedString(
    price: Int,
    oldPrice: Int,
): AnnotatedString {
    val headlineSmall = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold
    )
    val bodySmall = MaterialTheme.typography.bodySmall

    val priceText = stringResource(id = R.string.price, price)
    val oldPriceText = stringResource(id = R.string.price, oldPrice)
    val space = stringResource(id = R.string.space)

    return buildAnnotatedString {
        withStyle(headlineSmall.copy(color = MaterialTheme.colorScheme.background).toSpanStyle()) {
            append(priceText)
            append(space)
        }

        withStyle(
            bodySmall.copy(
                textDecoration = TextDecoration.LineThrough,
                color = ExtendedTheme.colorScheme.primaryVariant
            ).toSpanStyle()
        ) {
            if (oldPrice > price) {
                append(oldPriceText)
            }
        }
    }
}