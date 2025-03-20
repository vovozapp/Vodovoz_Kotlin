package com.vodovoz.app.design_system.composables.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme

@Composable
fun ProductListTitle(
    modifier: Modifier = Modifier,
    productsQuantity: String,
    title: String,
    onShareClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically)) {
            if(title.isNotBlank()) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if(productsQuantity.isNotBlank()) {
                Text(
                    text = productsQuantity,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.icon_share),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(24.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable { onShareClick() }
        )
    }
}

@Preview
@Composable
private fun ProductListTitlePreview() {
    VodovozTheme {
        ProductListTitle(
            modifier = Modifier.background(Color.White),
            productsQuantity = "23",
            title = "",
            onShareClick = {}
        )
    }
}