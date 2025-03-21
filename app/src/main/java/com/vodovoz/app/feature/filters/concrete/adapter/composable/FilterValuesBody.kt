package com.vodovoz.app.feature.filters.concrete.adapter.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.placeholders.EmptyResultPlaceholder
import com.vodovoz.app.design_system.model.filters.FilterValueUi
import com.vodovoz.app.util.TransliterationUtils

@Suppress("NonSkippableComposable")
@Composable
fun FilterValuesBody(
    modifier: Modifier = Modifier,
    searchQuery: String,
    filterValues: List<FilterValueUi>,
    onFilterValueSelect: (FilterValueUi) -> Unit,
) {
    val filteredValues = remember(filterValues, searchQuery) {
        val cyrillicSearchQuery = TransliterationUtils.latinToCyrillic(searchQuery)
        filterValues.filter { valueUi ->
            valueUi.name.contains(
                searchQuery,
                true
            ) || valueUi.name.contains(cyrillicSearchQuery, true)
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {

        if (filteredValues.isEmpty()) {
            item {
                EmptyResultPlaceholder(
                    title = stringResource(id = R.string.empty_filters_values_title),
                    description = stringResource(id = R.string.empty_filters_values_description)
                )
            }
        } else {
            itemsIndexed(
                items = filteredValues,
                key = { _, item -> item.id }
            ) { i, filterValue ->
                Column(modifier = Modifier.animateItem(fadeOutSpec = null)) {
                    FilterValueItem(
                        filterValue = filterValue,
                        onCheckedChange = onFilterValueSelect
                    )
                    if (i != filterValues.lastIndex) {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun FilterValueItem(
    modifier: Modifier = Modifier,
    filterValue: FilterValueUi,
    onCheckedChange: (FilterValueUi) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onCheckedChange(filterValue) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = filterValue.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Checkbox(
            checked = filterValue.selected,
            onCheckedChange = {
                onCheckedChange(filterValue)
            },
            colors = CheckboxDefaults.colors(
                checkmarkColor = MaterialTheme.colorScheme.background,
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

