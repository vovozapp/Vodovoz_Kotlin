package com.vodovoz.app.feature.buy_certificate.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.feature.buy_certificate.model.CertificateUi

@Suppress("NonSkippableComposable")
@Composable
fun CertificatesRow(
    modifier: Modifier = Modifier,
    title: String,
    error: Boolean,
    certificates: List<CertificateUi>,
    currentCertificate: CertificateUi,
    onCertificateClick: (CertificateUi) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 19.dp, end = 45.dp),
            text = title,
            color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )

        val itemsInRow = 3

        FlowRow(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = itemsInRow,
            maxLines = 3
        ) {
            certificates.forEach { certificate ->
                CertificateCard(
                    modifier = Modifier.weight(1f),
                    certificateUi = certificate,
                    selected = certificate == currentCertificate,
                    onClick = onCertificateClick
                )
            }
            repeat(3 - certificates.size % 3) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CertificateCard(
    modifier: Modifier = Modifier,
    certificateUi: CertificateUi,
    selected: Boolean,
    onClick: (CertificateUi) -> Unit,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .height(66.dp)
            .border(
                if (selected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                else BorderStroke(0.dp, Color.Transparent),
                MaterialTheme.shapes.large
            )
            .clickable { onClick(certificateUi) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(certificateUi.image)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .padding(4.dp)
                .matchParentSize()
                .clip(MaterialTheme.shapes.large),
            contentScale = ContentScale.Crop
        )
    }
}