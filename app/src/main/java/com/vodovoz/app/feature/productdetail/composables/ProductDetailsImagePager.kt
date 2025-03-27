package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mx.platacard.pagerindicator.PagerIndicatorOrientation
import mx.platacard.pagerindicator.PagerWormIndicator

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsImagePager(
    modifier: Modifier = Modifier,
    productImages: List<String>,
    onImageClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState { productImages.count() }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            beyondViewportPageCount = productImages.size,
            pageSpacing = 16.dp,
            snapPosition = SnapPosition.Start,
        ) { page ->
            AsyncImage(
                model = productImages[page],
                contentDescription = null,
                modifier = Modifier
                    .height(211.dp)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures { onImageClick(productImages[page]) }
                    },
                contentScale = ContentScale.FillHeight
            )
        }

        if(productImages.size > 1) {
            PagerWormIndicator(
                modifier = Modifier.padding(top = 8.dp),
                pagerState = pagerState,
                activeDotColor = MaterialTheme.colorScheme.primary,
                dotColor = MaterialTheme.colorScheme.surfaceVariant,
                dotCount = productImages.count(),
                orientation = PagerIndicatorOrientation.Horizontal,
                minDotSize = 5.dp,
                activeDotSize = 5.dp,
                space = 6.dp
            )
        }

    }

//    LaunchedEffect(pagerState) {
//        snapshotFlow { pagerState.currentPage }.collect { page ->
//            //todo - onChange page
//        }
//    }
}