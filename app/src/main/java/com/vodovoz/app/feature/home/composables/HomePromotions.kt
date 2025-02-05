package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.model.PromotionUi
import com.vodovoz.app.feature.home.model.SectionUi

@Composable
fun HomePromotions(
    modifier: Modifier = Modifier,
    onShowAllClick: () -> Unit,
    sectionPromotions: SectionUi<PromotionUi>,
    onPromotionClick: (PromotionUi) -> Unit,
) {
    val pagerState = rememberPagerState {
        sectionPromotions.items.count()
    }

    Column(modifier = modifier) {
        TitleAndButton(
            title = sectionPromotions.title,
            button = sectionPromotions.button,
            onShowAllClick = { onShowAllClick() }
        )

        AuthScrollImagePager(
            pagerState = pagerState,
            modifier = Modifier.padding(top = 16.dp),
            images = sectionPromotions.items.map { promotionUi -> promotionUi.picture },
            onImageClick = { onPromotionClick(sectionPromotions.items[pagerState.currentPage]) },
            pageWidth = 300.dp,
        )
    }
}