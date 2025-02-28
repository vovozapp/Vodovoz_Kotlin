package com.vodovoz.app.feature.all.promotions.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun PromotionsSkeletonPlaceholder(modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(6) {

                val width = rememberSaveable {
                    Random.nextInt(70..130)
                }

                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = Modifier
                        .width(width.dp)
                        .height(30.dp)
                )
            }
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(6) {
                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            }
        }
    }
}