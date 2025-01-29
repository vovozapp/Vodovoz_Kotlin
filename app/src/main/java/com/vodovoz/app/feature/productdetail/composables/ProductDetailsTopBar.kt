package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.ClickableIcon

@Composable
fun ProductDetailTopBar(
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    isFavoriteProduct: Boolean
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        contentColor = MaterialTheme.colorScheme.onBackground,
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClickableIcon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                modifier = Modifier.size(24.dp),
                onClick = { onNavigationClick() },
                tint = MaterialTheme.colorScheme.onBackground

            )
            Spacer(modifier = Modifier.weight(1f))

            ClickableIcon(
                painter = painterResource(id = if(isFavoriteProduct) R.drawable.ic_filled_like else R.drawable.ic_like),
                modifier = Modifier.size(24.dp),
                onClick = onLikeClick,
                tint = if(isFavoriteProduct) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground

            )
            ClickableIcon(
                painter = painterResource(id = R.drawable.ic_share),
                modifier = Modifier
                    .padding(start = 24.dp)
                    .size(24.dp),
                onClick = onShareClick,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
private fun ProductDetailTopBarPreview() {
    VodovozTheme {
        ProductDetailTopBar(onNavigationClick = { /*TODO*/ }, onLikeClick = { /*TODO*/ }, isFavoriteProduct = true, onShareClick = {})
    }
}