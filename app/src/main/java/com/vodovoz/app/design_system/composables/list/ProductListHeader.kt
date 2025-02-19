package com.vodovoz.app.design_system.composables.list

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.button.LayoutSwitchButton
import com.vodovoz.app.design_system.composables.button.SortingButton

@Composable
fun ProductListHeader(
    modifier: Modifier = Modifier,
    sortName: String,
    isGridView: Boolean,
    onSwitchClick: () -> Unit,
    onSortingClick: () -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
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
            onSwitch = { onSwitchClick() })
    }

}