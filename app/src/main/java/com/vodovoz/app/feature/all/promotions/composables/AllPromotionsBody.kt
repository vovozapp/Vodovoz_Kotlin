package com.vodovoz.app.feature.all.promotions.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox
import com.vodovoz.app.design_system.composables.tab_row.VodovozScrollableTabRow
import com.vodovoz.app.design_system.model.PromotionCategoryUi
import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.util.extensions.indexOfOrNull
import kotlin.random.Random

@Suppress("NonSkippableComposable")
@Composable
fun AllPromotionsBody(
    modifier: Modifier = Modifier,
    categories: List<PromotionCategoryUi>,
    currentCategory: PromotionCategoryUi,
    lazyPagingPromotions: LazyPagingItems<PromotionUi>,
    lazyListState: LazyListState,
    onSectionSelect: (PromotionCategoryUi) -> Unit,
    onAdvertisingClick: (PromotionUi) -> Unit,
    onPromotionClick: (PromotionUi) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        VodovozScrollableTabRow(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            selectedTabIndex = categories.indexOfOrNull(currentCategory) ?: 0,
            edgePadding = 16.dp,
            spacing = 8.dp
        ) {
            categories.forEach { section ->
                VodovozChip(
                    text = section.name,
                    selected = currentCategory == section,
                    onSelect = {
                        onSectionSelect(section)
                    }
                )
            }
        }



        val refreshState = lazyPagingPromotions.loadState.refresh
        val appendState = lazyPagingPromotions.loadState.append
        val shimmerState = rememberShimmer(shimmerBounds = ShimmerBounds.View)

        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = lazyListState
        ) {

            when (refreshState) {
                is LoadState.NotLoading -> {
                    items(count = lazyPagingPromotions.itemCount) { i ->
                        val promotion = lazyPagingPromotions[i]
                        if (promotion != null) {
                            PromotionCard(
                                promotion = promotion,
                                onClick = onPromotionClick,
                                onAdvertisingClick = onAdvertisingClick
                            )
                        }
                    }
                }

                else -> {
                    items(10) {
                        SkeletonBox(
                            shimmerState = shimmerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }
                }
            }

            if(appendState is LoadState.Loading){
                item {
                    SkeletonBox(
                        shimmerState = shimmerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                    )
                }
            }
        }
    }
}