package com.vodovoz.app.feature.cancel_order_fragment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.top_bar.ClosingTopBar
import com.vodovoz.app.feature.cancel_order_fragment.model.CancelOrderState

@Composable
fun CancelOrderScreen(viewModel: CancelOrderViewModel, viewState: CancelOrderState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ClosingTopBar(title = viewState.title) {
            viewModel.navigateBack()
        }
        Text(
            modifier = Modifier.padding(
                top = 8.dp,
                start = 16.dp,
                end = 16.dp
            ),
            text = viewState.description,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = viewState.warningText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(
                start = 10.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 24.dp
            )
        )

        viewState.checkboxesNames.forEach { checkboxName ->
            CheckBoxRow(
                name = checkboxName,
                selected = viewState.currentCheckboxName == checkboxName,
                onCheckedChange = { name ->
                    viewModel.changeCurrentCheckbox(name)
                }
            )
        }


    }
}

@Composable
private fun CheckBoxRow(
    modifier: Modifier = Modifier,
    name: String,
    selected: Boolean,
    onCheckedChange: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onCheckedChange(name) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Checkbox(
            checked = selected,
            onCheckedChange = {
                onCheckedChange(name)
            },
            colors = CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colorScheme.background,
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
