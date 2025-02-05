package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.BannerUi

@Suppress("NonSkippableComposable")
@Composable
fun HomeBanners(
    modifier: Modifier = Modifier,
    banners: List<BannerUi>,
    onBannerClick: (BannerUi) -> Unit,
) {
    val pagerState = rememberPagerState { banners.size }
    val pictures = banners.map { banner -> banner.detailPicture }

    if (pictures.size > 1) {
        AuthScrollImagePager(
            modifier = modifier.padding(top = 8.dp),
            images = pictures,
            onImageClick = { page ->
                onBannerClick(banners[page])
            },
            pagerState = pagerState,
            pageWidth = 315.dp
        )
    }
}