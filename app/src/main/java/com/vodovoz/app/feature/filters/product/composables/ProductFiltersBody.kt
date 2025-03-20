package com.vodovoz.app.feature.filters.product.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.slider.VodovozRangeSlider
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.design_system.model.filters.FilterUi
import com.vodovoz.app.design_system.model.filters.FilterValueUi
import com.vodovoz.app.design_system.model.filters.FiltersPriceUi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Suppress("NonSkippableComposable")
@Composable
fun ProductFiltersBody(
    modifier: Modifier = Modifier,
    sliderState: RangeSliderState,
    filterPrice: FiltersPriceUi,
    filters: List<FilterUi>,
    onPriceChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onFilterValueSelect: (FilterUi, FilterValueUi) -> Unit,
    onShowAllFilterValuesClick: (FilterUi) -> Unit,
    onFromChange: (String) -> Unit,
    onToChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            text = stringResource(R.string.price_and_currency),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )

        Row(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
            VodovozTextField(
                modifier = Modifier.weight(1f),
                value = filterPrice.currentMin.toString(),
                onValueChange = onFromChange,
                prefix = stringResource(R.string.from)
            )
            Spacer(modifier = Modifier.width(8.dp))
            VodovozTextField(
                modifier = Modifier.weight(1f),
                value = filterPrice.currentMax.toString(),
                onValueChange = onToChange,
                prefix = stringResource(R.string.to)
            )
        }

        VodovozRangeSlider(
            modifier = Modifier.padding(top = 10.dp, start = 2.dp, end = 2.dp),
            onValueChange = onPriceChange,
            state = sliderState
        )


        filters.forEachIndexed { _, filter ->
            key(filter.name + filter.id) {
                FilterItem(
                    filter = filter,
                    showButton = filter.totalValues > 6,
                    onFilterValueSelect = { _, value ->
                        onFilterValueSelect(filter, value)
                    },
                    onShowAllClick = {
                        onShowAllFilterValuesClick(filter)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterItem(
    modifier: Modifier = Modifier,
    filter: (FilterUi),
    showButton: Boolean,
    onFilterValueSelect: (FilterUi, FilterValueUi) -> Unit,
    onShowAllClick: (FilterUi) -> Unit,
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp)) {
            Text(
                modifier = Modifier.weight(1f),
                text = filter.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(10.dp))

            if (showButton) {
                Text(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onShowAllClick(filter) }
                        .padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.all),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        FlowRow(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterValues = filter.values
            filterValues.takeWhile { filterValue ->
                filterValue.selected || filterValues.indexOf(filterValue) < 6
            }.forEach { filterValue ->
                VodovozChip(
                    text = filterValue.name,
                    selected = filterValue.selected,
                    onSelect = { onFilterValueSelect(filter, filterValue) },
                    containerColor = if (filterValue.selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (filterValue.selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }

}