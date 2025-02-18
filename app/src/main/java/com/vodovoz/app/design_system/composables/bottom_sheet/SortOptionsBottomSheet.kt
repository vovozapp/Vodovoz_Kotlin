package com.vodovoz.app.design_system.composables.bottom_sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.button.VodovozRadioButton
import com.vodovoz.app.design_system.composables.button.VodovozRadioRow
import com.vodovoz.app.feature.product_comments.model.SortUi

@Suppress("NonSkippableComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOptionsBottomSheet(
    onDismissRequest: () -> Unit,
    state: SheetState = rememberModalBottomSheetState(true),
    currentSort: SortUi,
    sorting: List<SortUi>,
    onSortSelect: (SortUi) -> Unit,
) {
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismissRequest,
        dragHandle = {
            VodovozDragHandle()
        },
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
        ) {
            sorting.forEachIndexed { index, sort ->
                VodovozRadioRow(name = sort.name, selected = currentSort == sort, onSelect = { onSortSelect(sort) })

                if (index != sorting.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

