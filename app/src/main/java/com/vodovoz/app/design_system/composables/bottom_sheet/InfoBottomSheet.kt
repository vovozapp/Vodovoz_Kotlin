package com.vodovoz.app.design_system.composables.bottom_sheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoBottomSheet(
    title: String,
    text: String,
    onDismissRequest: () -> Unit,
    onApply: () -> Unit,
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(true),
        onDismissRequest = onDismissRequest,
        dragHandle = {
            VodovozDragHandle()
        },
        containerColor = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.sp
                )
            )

            VodovozButton(
                modifier = Modifier.padding(
                    top = 20.dp,
                    bottom = 16.dp
                ),
                text = stringResource(id = R.string.its_clear),
                onClick = onApply,
                colors = VodovozButtonDefaults.secondaryColors()
            )
        }
    }


}