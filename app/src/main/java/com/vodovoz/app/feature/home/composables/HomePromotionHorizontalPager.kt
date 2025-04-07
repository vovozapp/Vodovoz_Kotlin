package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Suppress("NonSkippableComposable")
@Composable
fun AuthScrollImagePager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    images: List<String>,
    pageWidth: Dp,
    onImageClick: (page: Int) -> Unit,
    chip: @Composable (page: Int) -> Unit = {},
) {
    val isDraggedState = pagerState.interactionSource.collectIsDraggedAsState()
    val context = LocalContext.current

    HorizontalPager(
        modifier = modifier.fillMaxWidth(),
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 8.dp,
        pageSize = if (pageWidth.isUnspecified) PageSize.Fill else PageSize.Fixed(pageWidth),
        verticalAlignment = Alignment.CenterVertically,
        beyondViewportPageCount = 1,
        snapPosition = SnapPosition.Start,

        ) { page ->

        val currentImage = images[page]

        Box {
            AsyncImage(
                model = ImageRequest.Builder(context).data(currentImage).crossfade(true).build(),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        onImageClick(page)
                    },
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopStart
            )
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                chip(page)
            }
        }
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

