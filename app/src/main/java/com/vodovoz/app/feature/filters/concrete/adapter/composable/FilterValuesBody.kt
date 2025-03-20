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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.placeholders.EmptyResultPlaceholder
import com.vodovoz.app.design_system.model.filters.FilterValueUi

@Suppress("NonSkippableComposable")
@Composable
fun FilterValuesBody(
    modifier: Modifier = Modifier,
    searchQuery: String,
    filterValues: List<FilterValueUi>,
    onFilterValueSelect: (FilterValueUi) -> Unit,
) {
    val filteredValues = filterValues.filter { valueUi ->
        valueUi.name.contains(searchQuery, true) || customTransliterate(valueUi.name).contains(searchQuery, true)
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
                key = { _, item -> item.name + item.id }
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

@Composable
private fun customTransliterate(text: String): String {
    val charMap = mapOf(
        'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g",
        'д' to "d", 'е' to "e", 'ё' to "yo", 'ж' to "zh",
        'з' to "z", 'и' to "i", 'й' to "y", 'к' to "k",
        'л' to "l", 'м' to "m", 'н' to "n", 'о' to "o",
        'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t",
        'у' to "u", 'ф' to "f", 'х' to "h", 'ц' to "ts",
        'ч' to "ch", 'ш' to "sh", 'щ' to "sch", 'ъ' to "",
        'ы' to "y", 'ь' to "", 'э' to "e", 'ю' to "yu",
        'я' to "ya",

        'А' to "A", 'Б' to "B", 'В' to "V", 'Г' to "G",
        'Д' to "D", 'Е' to "E", 'Ё' to "Yo", 'Ж' to "Zh",
        'З' to "Z", 'И' to "I", 'Й' to "Y", 'К' to "K",
        'Л' to "L", 'М' to "M", 'Н' to "N", 'О' to "O",
        'П' to "P", 'Р' to "R", 'С' to "S", 'Т' to "T",
        'У' to "U", 'Ф' to "F", 'Х' to "H", 'Ц' to "Ts",
        'Ч' to "Ch", 'Ш' to "Sh", 'Щ' to "Sch", 'Ъ' to "",
        'Ы' to "Y", 'Ь' to "", 'Э' to "E", 'Ю' to "Yu",
        'Я' to "Ya"
    )
    return text.map { char ->
        charMap[char] ?: char.toString()
    }.joinToString("")
}