package com.vodovoz.app.feature.catalog.composables

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.vodovoz.app.design_system.model.BannerUi
import com.vodovoz.app.feature.catalog.model.CatalogCategoryUi

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
    FlowRow(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp, top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 2
    ) {
        val cardModifier = Modifier.weight(1f)

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

        banners.forEach { banner ->
            CatalogCard(
                modifier = cardModifier,
                title = "",
                image = banner.detailPicture,
                onClick = {
                    onBannerClick(banner)
                }
            )
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
            model = image,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
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