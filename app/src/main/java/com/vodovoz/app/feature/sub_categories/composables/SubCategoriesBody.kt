package com.vodovoz.app.feature.sub_categories.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.model.ParentCategoryUi
import com.vodovoz.app.util.TransliterationUtils

@Composable
fun SubCategoriesBody(
    modifier: Modifier = Modifier,
    searchQuery: String,
    catalogCategory: ParentCategoryUi,
    onCategoryClick: (ParentCategoryUi) -> Unit,
    onParentCategoryClick: (ParentCategoryUi) -> Unit,
) {

    val childCategories = catalogCategory.childCategories
    val filteredCategories = remember(childCategories, searchQuery) {
        val cyrillicSearchQuery = TransliterationUtils.latinToCyrillic(searchQuery)

        childCategories.filter { category ->
            category.name.contains(searchQuery, true)
                    || category.name.contains(cyrillicSearchQuery, true)
        }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = catalogCategory.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        }
        item {
            SubCategoryItem(
                category = catalogCategory.copy(
                    name = stringResource(id = R.string.all),
                    childCategories = emptyList()
                ),
                onCategoryClick = {
                    onParentCategoryClick(catalogCategory)
                }
            )

        }
        items(
            items = filteredCategories,
            key = { categoryUi -> categoryUi.name + categoryUi.id }
        ) { category ->
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            SubCategoryItem(
                category = category,
                onCategoryClick = onCategoryClick,
                modifier = Modifier.animateItem(fadeOutSpec = null)
            )
        }
    }
}

@Composable
fun SubCategoryItem(
    modifier: Modifier = Modifier,
    category: ParentCategoryUi,
    onCategoryClick: (ParentCategoryUi) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onCategoryClick(category) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .width(4.dp)
        )
        if (category.childCategories.isNotEmpty()) {
            Icon(painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable {
                        onCategoryClick(category)
                    }
            )
        }
    }
}