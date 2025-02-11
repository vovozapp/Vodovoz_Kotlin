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
import com.vodovoz.app.design_system.model.BrandCategoryItemUi

@Composable
fun ProductDetailsCategoryAndBrand(
    modifier: Modifier = Modifier,
    category: BrandCategoryItemUi?,
    brand: BrandCategoryItemUi?,
    onBrandClick: (BrandCategoryItemUi) -> Unit,
    onCategoryClick: (BrandCategoryItemUi) -> Unit,
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        if (brand != null) {
            CategoryOrBrandItem(
                brandCategoryItemUi = brand,
                onClick = { onBrandClick(brand) })

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        if (category != null) {
            CategoryOrBrandItem(
                brandCategoryItemUi = category,
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
    brandCategoryItemUi: BrandCategoryItemUi,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = brandCategoryItemUi.data.detailPicture,
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
                text = brandCategoryItemUi.data.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = brandCategoryItemUi.title,
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