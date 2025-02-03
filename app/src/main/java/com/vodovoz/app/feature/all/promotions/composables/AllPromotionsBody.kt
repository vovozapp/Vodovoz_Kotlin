package com.vodovoz.app.feature.all.promotions.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.tab_row.VodovozTabRow
import com.vodovoz.app.design_system.model.PromotionSectionUi
import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.util.extensions.indexOfOrNull

@Suppress("NonSkippableComposable")
@Composable
fun AllPromotionsBody(
    modifier: Modifier = Modifier,
    sections: List<PromotionSectionUi>,
    currentSection: PromotionSectionUi,
    lazyPagingPromotions: LazyPagingItems<PromotionUi>,
    lazyListState: LazyListState,
    onSectionSelect: (PromotionSectionUi) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        VodovozTabRow(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            selectedTabIndex = sections.indexOfOrNull(currentSection) ?: 0,
            edgePadding = 16.dp,
            spacing = 8.dp
        ) {
            sections.forEach { section ->
                VodovozChip(
                    text = section.name,
                    selected = currentSection == section,
                    onSelect = {
                        onSectionSelect(section)
                    }
                )
            }
        }

        val promotionUiList = lazyPagingPromotions.itemSnapshotList.mapNotNull { promotionUi ->
            if (promotionUi?.sectionId != currentSection.id && currentSection.id != 0) {
                return@mapNotNull null
            } else promotionUi
        }


        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = lazyListState
        ) {
            items(items = promotionUiList, key = { it.id }) { promotion ->
                PromotionCard(promotion = promotion, onClick = { }, onAdvertisingClick = { })
            }
        }
    }
}