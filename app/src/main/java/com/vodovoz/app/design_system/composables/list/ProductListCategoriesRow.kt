package com.vodovoz.app.design_system.composables.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.tab_row.VodovozScrollableTabRow
import com.vodovoz.app.feature.home.model.CategoryUi
import com.vodovoz.app.util.extensions.indexOfOrNull

@Suppress("NonSkippableComposable")
@Composable
fun ProductListCategoriesRow(
    modifier: Modifier = Modifier,
    categories: List<CategoryUi>,
    showEmptyCategory: Boolean,
    currentCategory: CategoryUi,
    onCategoryClick: (CategoryUi) -> Unit,
    onCategoriesListClick: (() -> Unit)? = null,
) {
    VodovozScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = (categories.indexOfOrNull(currentCategory) ?: 0) + (onCategoriesListClick?.let { 1 } ?: 0),
        edgePadding = 16.dp,
        spacing = 8.dp
    ) {

        onCategoriesListClick?.let {
            Icon(
                painter = painterResource(id = R.drawable.icon_category),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onCategoriesListClick() }
            )
        }

        if(showEmptyCategory){
            VodovozChip(
                text = stringResource(id = R.string.all),
                selected = currentCategory == CategoryUi.Empty,
                onSelect = { onCategoryClick(CategoryUi.Empty) }
            )
        }

        categories.forEach { category ->
            VodovozChip(
                text = category.name,
                selected = category == currentCategory,
                onSelect = { onCategoryClick(category) }
            )
        }
    }
}