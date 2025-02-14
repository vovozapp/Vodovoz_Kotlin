package com.vodovoz.app.design_system.composables.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R

@Composable
fun SortingButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isDescending: Boolean = false,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_sort_18),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text,
            style = MaterialTheme.typography.bodySmall
        )


    }
}