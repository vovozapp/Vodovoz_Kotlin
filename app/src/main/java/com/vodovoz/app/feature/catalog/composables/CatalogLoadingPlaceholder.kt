package com.vodovoz.app.feature.catalog.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.decoration.SkeletonBox

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogLoadingPlaceholder(modifier: Modifier = Modifier) {
    val shimmerState = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        val boxModifier = Modifier
            .weight(1f)
            .aspectRatio(1.15f)

        SkeletonBox(
            shimmerState = shimmerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        repeat(4) {
            Row {
                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = boxModifier
                )
                Spacer(modifier = Modifier.width(8.dp))
                SkeletonBox(
                    shimmerState = shimmerState,
                    modifier = boxModifier
                )
            }
        }

    }
}

@Preview
@Composable
private fun CatalogLoadingPlaceholderPreview() {
    VodovozTheme {
        CatalogLoadingPlaceholder()
    }
}