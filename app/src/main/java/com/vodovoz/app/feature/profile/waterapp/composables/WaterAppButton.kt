package com.vodovoz.app.feature.profile.waterapp.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.ExtendedTheme
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults

@Composable
fun WaterAppButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FilledTonalButton(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
        colors = VodovozButtonDefaults.primaryColors(),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 16.dp),
        elevation = null
    ) {
        Text(text = stringResource(R.string.next), style = ExtendedTheme.typography.buttonMedium, maxLines = 1)
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.background
        )
    }
}