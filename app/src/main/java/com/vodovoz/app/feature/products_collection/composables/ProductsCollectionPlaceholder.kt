package com.vodovoz.app.feature.products_collection.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductsCollectionPlaceholder(modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                shimmerState = shimmerState,
                modifier = Modifier
                    .width(180.dp)
                    .height(40.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            SkeletonBox(
                shimmerState = shimmerState,
                modifier = Modifier.size(40.dp)
            )
        }

        FlowRow(
            modifier = Modifier.padding(vertical = 16.dp),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(8) {
                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = Modifier
                        .weight(1f)
                        .height(255.dp)
                )
            }
        }

    }
}