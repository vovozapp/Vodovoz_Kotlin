package com.vodovoz.app.feature.profile.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.vodovoz.app.feature.profile.model.ProfileMenuItemUi

@Suppress("NonSkippableComposable")
@Composable
fun ProfileMenuColumn(modifier: Modifier = Modifier, menuItems: List<ProfileMenuItemUi>, onItemClick: (ProfileMenuItemUi) -> Unit) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        menuItems.forEachIndexed { index, profileMenuItemUi ->
            ProfileMenuItem(item = profileMenuItemUi, onClick = onItemClick)
            if (index != menuItems.lastIndex) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}