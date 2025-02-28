package com.vodovoz.app.feature.promotiondetail.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox

@Composable
fun PromotionDetailsLoadingPlaceholder(modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmer(ShimmerBounds.View)
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        SkeletonBox(
            shimmerState = shimmerState, modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(150.dp)
        )

        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SkeletonBox(
                shimmerState = shimmerState, modifier = Modifier
                    .width(60.dp)
                    .height(30.dp)
            )
            SkeletonBox(
                shimmerState = shimmerState, modifier = Modifier
                    .width(130.dp)
                    .height(30.dp)
            )

        }


//        val widths = listOf(1f, 0.9f, 0.75f, 0.85f)

//        widths.forEachIndexed { index, width ->
//            SkeletonBox(
//                shimmerState = shimmerState,
//                modifier = Modifier
//                    .padding(top = if (index == 0) 24.dp else 16.dp)
//                    .fillMaxWidth(width)
//                    .height(30.dp)
//            )
//        }
        SkeletonBox(
            shimmerState = shimmerState,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(1f)
                .height(230.dp)
        )


        SkeletonBox(
            shimmerState = shimmerState, modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth()
                .height(40.dp)
        )

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SkeletonBox(

                shimmerState = shimmerState, modifier = Modifier
                    .weight(1f)
                    .height(300.dp)
            )

            SkeletonBox(

                shimmerState = shimmerState, modifier = Modifier
                    .weight(1f)
                    .height(300.dp)
            )
        }
    }
}