package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme

@Composable
fun CartCounterButton(
    modifier: Modifier = Modifier,
    cartQuantity: Int,
    catalogQuantity: Int,
    haveTrash: Boolean = true,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    onTrashClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(38.dp)
            .width(120.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Box(
            modifier = Modifier
                .then(
                    if (cartQuantity < 1) Modifier else Modifier.clickable(
                        onClick = if (cartQuantity == 1 && haveTrash) onTrashClick else onMinusClick
                    )
                )
                .fillMaxHeight()
                .padding(start = 8.dp, end = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = if (cartQuantity <= 1 && haveTrash) R.drawable.icon_trash else R.drawable.ic_minus),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (cartQuantity > 0) MaterialTheme.colorScheme.primary
                else ExtendedTheme.colorScheme.primaryVariant
            )
        }

        Text(
            text = cartQuantity.toString(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall
        )

        Box(
            modifier = Modifier
                .then(if (cartQuantity < catalogQuantity) Modifier.clickable(onClick = onPlusClick) else Modifier)
                .fillMaxHeight()
                .padding(start = 4.dp, end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (cartQuantity < catalogQuantity)
                    MaterialTheme.colorScheme.primary
                else ExtendedTheme.colorScheme.primaryVariant
            )
        }

    }
}