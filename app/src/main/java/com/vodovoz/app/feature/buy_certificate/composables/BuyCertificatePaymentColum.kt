package com.vodovoz.app.feature.buy_certificate.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.design_system.composables.button.VodovozRadioButton
import com.vodovoz.app.design_system.model.PaymentTypeUi

@Suppress("NonSkippableComposable")
@Composable
fun BuyCertificatePaymentColum(
    modifier: Modifier = Modifier,
    title: String,
    error: Boolean,
    paymentTypes: List<PaymentTypeUi>,
    currentPaymentType: PaymentTypeUi,
    onPaymentTypeClick: (PaymentTypeUi) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 19.dp, end = 45.dp),
            text = title,
            color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        paymentTypes.forEachIndexed { index, paymentType ->
            Column {
                PaymentTypeItemCard(
                    paymentTypeUi = paymentType,
                    onClick = onPaymentTypeClick,
                    selected = paymentType == currentPaymentType
                )
                if (index != paymentTypes.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentTypeItemCard(
    modifier: Modifier = Modifier,
    paymentTypeUi: PaymentTypeUi,
    selected: Boolean,
    onClick: (PaymentTypeUi) -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .clickable(
                onClick = { onClick(paymentTypeUi) },
                interactionSource = null,
                indication = null
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = ImageRequest.Builder(context)
                .crossfade(true)
                .data(paymentTypeUi.image)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.FillBounds
        )

        Text(
            text = paymentTypeUi.name,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )

        VodovozRadioButton(selected = selected, onClick = { })
    }
}



