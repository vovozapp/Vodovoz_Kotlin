package com.vodovoz.app.feature.home.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox
import com.vodovoz.app.design_system.composables.top_bar.SearchTopBar


@Composable
fun HomeSkeletonPlaceholder(modifier: Modifier = Modifier, onScanClick: () -> Unit = {}) {
    Column {
        val shimmerState = rememberShimmer(ShimmerBounds.View)

        SearchTopBar(
            value = "",
            onValueChange = {},
            onFocus = { },
            onMicClick = { },
            onScanClick = onScanClick,
            onSearchClick = {}
        )


        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(2) {
                    SkeletonBox(
                        modifier = Modifier
                            .width(315.dp)
                            .height(150.dp),
                        shimmerState = shimmerState
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(2) {
                    SkeletonBox(
                        modifier = Modifier
                            .width(230.dp)
                            .height(72.dp),
                        shimmerState = shimmerState

                    )
                }
            }
            HomeDivider(
                modifier = Modifier.padding(top = 4.dp)
            )

            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp)
                    .height(24.dp)
                    .clip(MaterialTheme.shapes.medium),
                shimmerState = shimmerState

            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(8) {
                    SkeletonBox(
                        modifier = Modifier
                            .width(75.dp)
                            .height(96.dp),
                        shimmerState = shimmerState

                    )
                }
            }

            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp)
                    .height(24.dp)
                    .clip(MaterialTheme.shapes.medium),
                shimmerState = shimmerState

            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) {
                    SkeletonBox(
                        modifier = Modifier
                            .width(88.dp)
                            .height(30.dp),
                        shimmerState = shimmerState

                    )
                }

            }


            repeat(6) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(4) {
                        SkeletonBox(
                            modifier = Modifier
                                .width(160.dp)
                                .height(255.dp),
                            shimmerState = shimmerState

                        )
                    }
                }

                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 32.dp)
                        .height(24.dp)
                        .clip(MaterialTheme.shapes.medium),
                    shimmerState = shimmerState
                )
            }

        }
    }
}

@Preview
@Composable
private fun HomeSkeletonPlaceholderPreview() {
    VodovozTheme {
        HomeSkeletonPlaceholder()
    }
}


@Composable
fun ShimmeringPlaceholder() {
    Row(
        modifier = Modifier
            .shimmer() // <- Affects all subsequent UI elements
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(80.dp, 80.dp)
                .background(Color.LightGray),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(Color.LightGray),
            )
            Box(
                modifier = Modifier
                    .size(120.dp, 20.dp)
                    .background(Color.LightGray),
            )
        }
    }
}

@Preview
@Composable
private fun ShimmeringPlaceholderPreview() {
    VodovozTheme {
        ShimmeringPlaceholder()
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
) {


    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 1.0f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    Box(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(brush)
        )
    }


}