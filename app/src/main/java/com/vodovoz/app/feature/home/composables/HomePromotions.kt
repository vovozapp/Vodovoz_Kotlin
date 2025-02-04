package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.model.PromotionUi

@Suppress("NonSkippableComposable")
@Composable
fun HomePromotions(
    modifier: Modifier = Modifier,
    onShowAllClick: () -> Unit,
    promotions: List<PromotionUi>,
    onPromotionClick: (PromotionUi) -> Unit,
) {
    val pagerState = rememberPagerState {
        promotions.count()
    }

    Column(modifier = modifier) {
        HomeTitleAndAll(
            title = stringResource(R.string.promotions),
            onShowAllClick = onShowAllClick
        )

        AuthScrollImagePager(
            pagerState = pagerState,
            modifier = Modifier.padding(top = 16.dp),
            images = promotions.map { promotionUi -> promotionUi.picture },
            onImageClick = { onPromotionClick(promotions[pagerState.currentPage]) },
            pageWidth = 300.dp,
        )
    }
}