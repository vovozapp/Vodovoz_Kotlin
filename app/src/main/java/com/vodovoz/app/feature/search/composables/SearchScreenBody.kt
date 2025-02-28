package com.vodovoz.app.feature.search.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.feature.home.model.ProductUi
import com.vodovoz.app.feature.home.model.SectionUi

@Suppress("NonSkippableComposable")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreenBody(
    modifier: Modifier = Modifier,
    matchingQueries: List<String>,
    sectionRecommendations: SectionUi<ProductUi>,
    onQueryChoose: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        FlowRow(
            modifier = Modifier.padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxLines = 5
        ) {
            matchingQueries.forEach { query ->
                VodovozChip(text = query, selected = false, onSelect = { onQueryChoose(query) })
            }
        }

        val title = sectionRecommendations.title
        if (title.isNotBlank()) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 24.dp)
            )
        }

        val products = sectionRecommendations.items
        FlowRow(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            products.forEach { product ->
                GridProductCard(
                    modifier = Modifier.weight(1f),
                    product = product,
                    onClick = {

                    },
                    onLike = {

                    },
                    onAnalogsClick = {

                    }
                )
            }
            if (products.size % 2 == 1) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}