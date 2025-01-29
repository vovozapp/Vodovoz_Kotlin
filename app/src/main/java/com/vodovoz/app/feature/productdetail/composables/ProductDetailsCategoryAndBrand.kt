package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.ui.model.BrandUI
import com.vodovoz.app.ui.model.CategoryUI

@Composable
fun ProductDetailsCategoryAndBrand(
    modifier: Modifier = Modifier,
    category: CategoryUI?,
    brand: BrandUI?,
    onBrandClick: (BrandUI) -> Unit,
    onCategoryClick: (CategoryUI) -> Unit,
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        if (brand != null) {
            CategoryOrBrandItem(
                imageUrl = brand.detailPicture,
                name = brand.name,
                isCategory = false,
                onClick = { onBrandClick(brand) })

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        if (category != null) {
            CategoryOrBrandItem(
                imageUrl = category.detailPicture ?: "",
                name = category.name,
                isCategory = true,
                onClick = { onCategoryClick(category) }
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }

    }
}

@Composable
private fun CategoryOrBrandItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    name: String,
    isCategory: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (isCategory) stringResource(id = R.string.category) else stringResource(id = R.string.brand),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceTint,
            modifier = Modifier.size(24.dp)
        )
    }
}