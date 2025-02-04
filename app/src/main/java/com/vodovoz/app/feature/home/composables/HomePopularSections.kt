package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vodovoz.app.feature.home.model.PopularCategoryUi
import com.vodovoz.app.feature.home.model.SectionUi

@Composable
fun HomePopularSections(
    modifier: Modifier = Modifier,
    sectionPopularCategories: SectionUi<PopularCategoryUi>,
    onPopularCategoryClick: (PopularCategoryUi) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = sectionPopularCategories.title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sectionPopularCategories.items.forEach { section ->
                HomeSectionItem(section = section, onClick = onPopularCategoryClick)
            }
        }
    }
}

@Composable
fun HomeSectionItem(
    modifier: Modifier = Modifier,
    section: PopularCategoryUi,
    onClick: (PopularCategoryUi) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(75.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick(section) }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(75.dp)
                .height(72.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.large
                )
        ) {
            AsyncImage(
                model = section.image,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.FillBounds
            )
        }

        val labelSmall = MaterialTheme.typography.labelSmall
        val fontSize = (labelSmall.fontSize.value - 1).sp

        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 1.dp),
            text = section.name,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            style = labelSmall.copy(fontSize = fontSize, letterSpacing = 0.1.sp),
            overflow = TextOverflow.Ellipsis
        )
    }
}

