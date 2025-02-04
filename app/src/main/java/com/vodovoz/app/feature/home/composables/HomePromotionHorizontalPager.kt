package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.compose.rememberConstraintsSizeResolver
import coil3.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Suppress("NonSkippableComposable")
@Composable
fun AuthScrollImagePager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    images: List<String>,
    onImageClick: (String) -> Unit,
    pageWidth: Dp,
) {


    val isDraggedState = pagerState.interactionSource.collectIsDraggedAsState()

    HorizontalPager(
        modifier = modifier.fillMaxWidth(),
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 8.dp,
        key = { page ->
            images[page]
        },
        pageSize = PageSize.Fixed(pageWidth),
        verticalAlignment = Alignment.CenterVertically,
        beyondViewportPageCount = 1,
        snapPosition = SnapPosition.Center,

        ) { page ->

        val currentImage = images[page]

        AsyncImage(
            model = currentImage,
            contentDescription = null,
            modifier = Modifier
                .height(150.dp)
                .width(300.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable {
                    onImageClick(currentImage)
                },
            contentScale = ContentScale.FillBounds
        )
    }

    LaunchedEffect(isDraggedState) {
        snapshotFlow { isDraggedState.value }
            .collectLatest { isDragged ->
                if (!isDragged) {
                    while (true) {
                        delay(4_500L)
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

//@Preview
//@Composable
//private fun PromotionHorizontalPager() {
//    VodovozTheme {
//        PromotionHorizontalPager(
//            promotion = listOf(
//                "https://vodovoz.net/upload/iblock/d9c/3yue4g53w2d79bukaaz7u0u8ym7b95tr.jpg",
//                "https://vodovoz.net/upload/iblock/8f9/sazr2hm139ok02s3oj80q0tsqr5f3ibf.jpg",
//                "https://vodovoz.net/upload/iblock/1eb/xgy856ctgho2l3bn8fzjcmqeug0tvbiw.jpg"
//            ),
//            onImageClick = {
//
//            }
//        )
//    }
//}