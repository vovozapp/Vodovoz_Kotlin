package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.design_system.model.ProductMediaUi
import mx.platacard.pagerindicator.PagerIndicatorOrientation
import mx.platacard.pagerindicator.PagerWormIndicator

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsMediaPager(
    modifier: Modifier = Modifier,
    productMediaList: List<ProductMediaUi>,
    onMediaClick: (ProductMediaUi) -> Unit,
) {

    val pagerState = rememberPagerState { productMediaList.size }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        HorizontalPager(
            state = pagerState,
            //contentPadding = PaddingValues(horizontal = 16.dp),
            beyondViewportPageCount = productMediaList.size,
            pageSpacing = 16.dp,
            snapPosition = SnapPosition.Start,
            pageSize = PageSize.Fill
        ) { page ->
            val media = productMediaList.getOrNull(page) ?: return@HorizontalPager


            Box {
                AsyncImage(
                    model = when (media) {
                        is ProductMediaUi.Picture -> media.url
                        is ProductMediaUi.Video -> media.video.previewImage
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .height(211.dp)
                        .fillMaxWidth()
                        .clickable {
                            onMediaClick(media)
                        },
                    contentScale = when (media) {
                        is ProductMediaUi.Picture -> ContentScale.FillHeight
                        is ProductMediaUi.Video -> ContentScale.Crop
                    }
                )

                when (media) {
                    is ProductMediaUi.Picture -> {}
                    is ProductMediaUi.Video -> Image(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(42.dp)
                            .clip(CircleShape),
                        painter = painterResource(id = R.drawable.pic_play),
                        contentDescription = null
                    )

                }
            }
        }

        if (productMediaList.size > 1) {
            PagerWormIndicator(
                modifier = Modifier.padding(top = 8.dp),
                pagerState = pagerState,
                activeDotColor = MaterialTheme.colorScheme.primary,
                dotColor = MaterialTheme.colorScheme.surfaceVariant,
                dotCount = productMediaList.count(),
                orientation = PagerIndicatorOrientation.Horizontal,
                minDotSize = 5.dp,
                activeDotSize = 5.dp,
                space = 6.dp
            )
        }

    }
}

enum class ProductDetailsPagerElement {

}