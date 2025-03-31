package com.vodovoz.app.design_system.composables.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.LayoutSwitchButton
import com.vodovoz.app.design_system.composables.button.SortingButton

@Composable
fun ProductListOptionsRow(
    modifier: Modifier = Modifier,
    sortName: String,
    isGridView: Boolean,
    onSwitchClick: () -> Unit,
    onSortingClick: () -> Unit,
    onFiltersClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .clickable(indication = null, interactionSource = null, onClick = {})
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SortingButton(
            text = sortName,
            onClick = {
                onSortingClick()
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        LayoutSwitchButton(
            isGridView = isGridView,
            onSwitch = { onSwitchClick() }
        )

        onFiltersClick?.let {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onFiltersClick() }
            )
        }
    }

}