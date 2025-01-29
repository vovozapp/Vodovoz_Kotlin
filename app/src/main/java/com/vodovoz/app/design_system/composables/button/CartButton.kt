package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme

@Composable
fun CartButton(modifier: Modifier = Modifier, count: Int, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.large
            )
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_cart),
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .size(24.dp)
                .wrapContentSize(unbounded = true)
                .offset(),
            tint = MaterialTheme.colorScheme.background
        )

        if (count > 0) {
            Box(
                modifier = Modifier
                    .defaultMinSize(24.dp, 24.dp)
                    .align(Alignment.TopEnd)
                    .offset(4.dp, (-11).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }

}

@Preview
@Composable
private fun ProductCounterButtonPreview() {
    VodovozTheme {
        ProductQuantityButton(
            isLoading = false,
            onPlus = { },
            onMinus = { },
            quantity = 2,
            totalPrice = 300
        )
    }
}