package com.vodovoz.app.feature.all.promotions.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.bottom_sheet.VodovozDragHandle
import com.vodovoz.app.design_system.model.AboutAdvertisingUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvertisingInfoBottomSheet(
    advertising: AboutAdvertisingUi,
    state: SheetState,
    onDismissRequest: () -> Unit,
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
        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .padding(horizontal = 16.dp),
            text = advertising.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = advertising.aboutCompanyTitle,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = advertising.aboutCompany,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surfaceTint
                )
            }
        }
    }
}