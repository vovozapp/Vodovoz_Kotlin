package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.util.formatPrice

@Composable
fun BaseQuantityButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    iconPadding: PaddingValues,
    iconSize: Dp,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    minusEnabled: Boolean = true,
    plusEnabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.background
    ),
    content: @Composable () -> Unit,
) {

    val iconModifier = Modifier
        .padding(iconPadding)
        .size(iconSize)

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = colors.containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            CounterButton(
                painter = painterResource(id = R.drawable.ic_minus),
                iconModifier = iconModifier,
                enabled = minusEnabled,
                isLoading = isLoading,
                color = colors.contentColor,
                disabledColor = colors.disabledContentColor,
                onClick = {
                    onMinus()
                }
            )

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                content()
            }

            CounterButton(
                painter = painterResource(id = R.drawable.ic_minus),
                iconModifier = iconModifier,
                enabled = plusEnabled,
                isLoading = isLoading,
                color = colors.contentColor,
                disabledColor = colors.disabledContentColor,
                onClick = {
                    onPlus()
                }
            )
        }
    }

}

@Composable
private fun CounterButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean,
    isLoading: Boolean,
    color: Color,
    disabledColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .fillMaxHeight()
            .clickable {
                if (!isLoading && enabled) {
                    onClick()
                }
            }, contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = iconModifier,
            tint = if (enabled) {
                color
            } else disabledColor

        )
    }
}

@Composable
fun QuantityButtonSmall(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    quantity: Int,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
) {
    BaseQuantityButton(
        modifier = modifier.height(38.dp),
        isLoading = isLoading,
        onPlus = { onPlus() },
        onMinus = { onMinus() },
        iconSize = 18.dp,
        iconPadding = PaddingValues(horizontal = 8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = MaterialTheme.colorScheme.background,
                trackColor = Color.Transparent,
                strokeWidth = 1.5.dp
            )
        } else Text(
            text = quantity.toString(),
            color = MaterialTheme.colorScheme.background,
            style = ExtendedTheme.typography.buttonSmall
        )
    }
}

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

    BaseQuantityButton(
        modifier = modifier.height(48.dp),
        isLoading = isLoading,
        onPlus = onPlus,
        onMinus = onMinus,
        iconPadding = PaddingValues(horizontal = 16.dp),
        iconSize = 24.dp
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.background,
                trackColor = Color.Transparent,
                strokeWidth = 1.5.dp
            )
        } else {
            Text(text = priceAnnotatedString, color = MaterialTheme.colorScheme.background)
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
private fun QuantityButtonSmallPreview() {
    VodovozTheme {
        QuantityButtonSmall(isLoading = false, quantity = 3, onPlus = { /*TODO*/ }) {

        }
    }
}

@Preview
@Composable
private fun ProductQuantityButtonPreview() {
    VodovozTheme {
        ProductQuantityButton(
            isLoading = false,
            onPlus = { },
            onMinus = { },
            quantity = 0,
            totalPrice = 500
        )
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



