package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mx.platacard.pagerindicator.PagerIndicatorOrientation
import mx.platacard.pagerindicator.PagerWormIndicator

@OptIn(ExperimentalFoundationApi::class)
@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsImagePager(
    modifier: Modifier = Modifier,
    productImages: List<String>,
    onImageClick: (String) -> Unit,
) {
    val pagerState = rememberPagerState { productImages.count() }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        HorizontalPager(
            modifier = modifier,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            beyondViewportPageCount = 2,
            pageSpacing = 16.dp,
            key = { page ->
                productImages[page]
            }
        ) { page ->
            AsyncImage(
                model = productImages[page],
                contentDescription = null,
                modifier = Modifier
                    .height(211.dp)
                    .fillMaxWidth()
                    .clickable(indication = null, interactionSource = null) {
                        onImageClick(
                            productImages[page]
                        )
                    },
                contentScale = ContentScale.FillHeight
            )
        }

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

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            //todo - onChange page
        }
    }
}