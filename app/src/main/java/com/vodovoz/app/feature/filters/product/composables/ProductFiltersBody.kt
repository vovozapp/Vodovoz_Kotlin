package com.vodovoz.app.feature.filters.product.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.slider.VodovozRangeSlider
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextField
import com.vodovoz.app.design_system.model.filters.FilterUi
import com.vodovoz.app.design_system.model.filters.FiltersPriceUi

@Suppress("NonSkippableComposable")
@Composable
fun ProductFiltersBody(
    modifier: Modifier = Modifier,
    filterPrice: FiltersPriceUi,
    filters: List<FilterUi>,
    onPriceChanged: (ClosedFloatingPointRange<Float>) -> Unit
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
                onValueChange = { }
            )
            Spacer(modifier = Modifier.width(8.dp))
            VodovozTextField(
                modifier = Modifier.weight(1f),
                value = filterPrice.currentMax.toString(),
                onValueChange = { }
            )
        }

        VodovozRangeSlider(
            modifier = Modifier.padding(top = 10.dp, start = 2.dp, end = 2.dp),
            onValueChanged = onPriceChanged
        )


        filters.forEachIndexed { index, filter ->
            if (index == 0) {
//                Text(
//                    modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp),
//                    text = filter.name,
//                    color = MaterialTheme.colorScheme.onBackground,
//                    style = MaterialTheme.typography.headlineSmall
//                )
//                FlowRow(
//                    modifier = Modifier.padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    filter.values.forEach { value ->
//                        VodovozChip(text = value.name, selected = false)
//                    }
//                }
//                Row(
//                    modifier = Modifier
//                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
//                        .clickable(
//                            interactionSource = null,
//                            indication = null,
//                            onClick = {
//
//                            }
//                        )
//                ) {
//                    Text(
//                        text = stringResource(R.string.see_more),
//                        color = MaterialTheme.colorScheme.primary,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_arrow_down),
//                        contentDescription = null,
//                        modifier = Modifier.size(24.dp),
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }

                Spacer(modifier = Modifier.height(16.dp))
                FilterItem(filter = filter) {}
            } else {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                FilterItem(filter = filter) {}
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun FilterItem(
    modifier: Modifier = Modifier,
    filter: (FilterUi),
    onFilterClick: (FilterUi) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onFilterClick(filter) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = filter.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(
            modifier = Modifier
                .width(16.dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable { onFilterClick(filter) },
            tint = MaterialTheme.colorScheme.surfaceTint
        )
    }
}