package com.vodovoz.app.feature.search.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox
import kotlin.random.Random
import kotlin.random.nextInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchLoadingPlaceholder(modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        FlowRow(
            modifier = modifier.padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxLines = 3
        ) {
            repeat(15) {
                val randomWidth = rememberSaveable {
                    Random.nextInt(60..120)
                }
                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = Modifier
                        .width(randomWidth.dp)
                        .height(30.dp)
                )
            }
        }

        SkeletonBox(
            shimmerState = shimmerState,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(0.7f)
                .height(24.dp)
        )

        FlowRow(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            repeat(6) {
                SkeletonBox(
                    shimmerState = shimmerState, modifier = Modifier
                        .weight(1f)
                        .height(255.dp)
                )
            }
        }

    }
}

@Preview
@Composable
private fun SearchLoadingPlaceholderPreview() {
    VodovozTheme {
        SearchLoadingPlaceholder()
    }
}