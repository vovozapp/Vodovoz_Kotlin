package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Suppress("NonSkippableComposable")
@Composable
fun AuthScrollImagePager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    images: List<String>,
    onImageClick: (page: Int) -> Unit,
    pageWidth: Dp,
) {
    val isDraggedState = pagerState.interactionSource.collectIsDraggedAsState()

    HorizontalPager(
        modifier = modifier.fillMaxWidth(),
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 8.dp,
        pageSize = if (pageWidth.isUnspecified) PageSize.Fill else PageSize.Fixed(pageWidth),
        verticalAlignment = Alignment.CenterVertically,
        beyondViewportPageCount = images.size,
        snapPosition = SnapPosition.Start,

        ) { page ->

        val currentImage = images[page]

        AsyncImage(
            model = currentImage,
            contentDescription = null,
            modifier = Modifier
                .height(150.dp)
                .width(pageWidth)
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    onImageClick(page)
                },
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopStart
        )
    }

    LaunchedEffect(isDraggedState) {
        snapshotFlow { isDraggedState.value }
            .collectLatest { isDragged ->
                if (!isDragged) {
                    while (true) {
                        delay(3_750L)
                        runCatching {
                            val targetPage = pagerState.currentPage.inc() % pagerState.pageCount
                            if (targetPage == 0) {
                                pagerState.scrollToPage(targetPage)
                            } else pagerState.animateScrollToPage(targetPage)
                        }
                    }
                }
            }
    }
}

