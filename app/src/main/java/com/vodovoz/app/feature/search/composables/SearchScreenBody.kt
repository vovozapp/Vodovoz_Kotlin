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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.card.GridProductCard
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.chip.VodovozClosableChip
import com.vodovoz.app.design_system.model.ProductUi
import com.vodovoz.app.design_system.model.SectionUi

@Suppress("NonSkippableComposable")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreenBody(
    modifier: Modifier = Modifier,
    matchingQueries: List<String>,
    searchHistory: List<String>,
    sectionRecommendations: SectionUi<ProductUi>,
    onQueryChoose: (String) -> Unit,
    onQueryClose: (String) -> Unit,
    onProductLikeClick: (ProductUi) -> Unit,
    onProductCardClick: (ProductUi) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        if (searchHistory.isNotEmpty()) {
            Text(
                text = stringResource(R.string.you_were_looking),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 14.dp)
            )

            FlowRow(
                modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxLines = 2
            ) {
                searchHistory.forEach { query ->
                    VodovozClosableChip(text = query, selected = false, onSelect = { onQueryChoose(query) }, onClose = { onQueryClose(query)})
                }
            }
        }



        if (matchingQueries.isNotEmpty()) {
            Text(
                text = stringResource(R.string.popularity),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 14.dp)
            )


            FlowRow(
                modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxLines = 5
            ) {
                matchingQueries.forEach { query ->
                    VodovozChip(text = query, selected = false, onSelect = { onQueryChoose(query) })
                }
            }
        }

        val title = sectionRecommendations.title
        val products = sectionRecommendations.items

        if (title.isNotBlank() && products.isNotEmpty()) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 14.dp)
            )
        }


        if (products.isNotEmpty()) {
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
                        onClick = onProductCardClick,
                        onLike = onProductLikeClick,
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
}