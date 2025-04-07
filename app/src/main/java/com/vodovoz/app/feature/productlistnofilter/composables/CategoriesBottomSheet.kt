package com.vodovoz.app.feature.productlistnofilter.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozRadioButton
import com.vodovoz.app.design_system.model.ParentCategoryUi

@Suppress("NonSkippableComposable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesBottomSheet(
    state: SheetState = rememberModalBottomSheetState(true),
    categories: List<ParentCategoryUi>,
    currentCategory: ParentCategoryUi,
    onDismissRequest: () -> Unit,
    onCategoryClick: (ParentCategoryUi) -> Unit,
    onCategoryChoose: () -> Unit,
) {
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismissRequest,
        dragHandle = {

        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .systemBarsPadding()
            .padding(start = 8.dp, end = 8.dp, top = 16.dp),
        shape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        contentWindowInsets = {
            WindowInsets(0, 0, 0, 0)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(600.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(24.dp))
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    text = stringResource(id = R.string.categories),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { onDismissRequest() },
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            RecursiveCategoryList(
                modifier = Modifier
                    .padding(
                        top = 24.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                categories = categories,
                currentCategory = currentCategory,
                onCategoryClick = onCategoryClick
            )

            VodovozButton(
                text = stringResource(id = R.string.choose),
                onClick = onCategoryChoose,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            )
        }
    }

}

@Suppress("NonSkippableComposable")
@Composable
private fun RecursiveCategoryList(
    modifier: Modifier = Modifier,
    categories: List<ParentCategoryUi>,
    currentCategory: ParentCategoryUi,
    level: Int = 0,
    onCategoryClick: (ParentCategoryUi) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = (level * 16).dp)
    ) {
        categories.forEach { category ->
            CategoryItem(
                category = category,
                selected = category.id == currentCategory.id,
                onCategorySelect = onCategoryClick
            )
            if (category.childCategories.isNotEmpty()) {
                RecursiveCategoryList(
                    categories = category.childCategories,
                    level = level + 1,
                    currentCategory = currentCategory,
                    onCategoryClick = onCategoryClick
                )
            }
        }
    }
}

@Composable
private fun CategoryItem(
    modifier: Modifier = Modifier,
    category: ParentCategoryUi,
    selected: Boolean,
    onCategorySelect: (ParentCategoryUi) -> Unit,
) {
    Column(
        modifier = modifier.clickable(
            interactionSource = null,
            indication = null
        ) { onCategorySelect(category) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = category.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            VodovozRadioButton(
                modifier = Modifier.padding(start = 4.dp),
                selected = selected,
                onClick = {
                    onCategorySelect(category)
                }
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceVariant,
            thickness = 1.dp
        )
    }
}