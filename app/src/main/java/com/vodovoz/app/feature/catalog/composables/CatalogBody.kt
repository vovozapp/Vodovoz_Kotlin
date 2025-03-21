package com.vodovoz.app.feature.catalog.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vodovoz.app.design_system.model.BannerUi
import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi
import com.vodovoz.app.feature.home.composables.AuthScrollImagePager

@OptIn(ExperimentalLayoutApi::class)
@Suppress("NonSkippableComposable")
@Composable
fun CatalogBody(
    modifier: Modifier = Modifier,
    categories: List<CatalogCategoryUi>,
    banners: List<BannerUi>,
    onBannerClick: (BannerUi) -> Unit,
    onCategoryClick: (CatalogCategoryUi) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        val pictures = banners.map { it.detailPicture }
        val pagerState = rememberPagerState(0) { pictures.size }

        if(banners.size > 1){
            AuthScrollImagePager(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(68.dp),
                images = pictures,
                onImageClick = { page ->
                    onBannerClick(banners[page])
                },
                pagerState = pagerState,
                pageWidth = Dp.Unspecified
            )
        }


        val cardModifier = Modifier.weight(1f)

        FlowRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            categories.forEach { category ->
                CatalogCard(
                    modifier = cardModifier,
                    title = category.name,
                    image = category.picture,
                    onClick = {
                        onCategoryClick(category)
                    }
                )
            }

            if(categories.size % 2 == 1){
                Spacer(modifier = cardModifier)
            }
        }
    }
}

@Composable
private fun CatalogCard(
    title: String,
    image: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {


    Box(
        modifier = modifier
            .height(140.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomEnd
        )
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}