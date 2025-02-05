package com.vodovoz.app.feature.home.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.vodovoz.app.design_system.composables.card.VodovozOutlinedCard
import com.vodovoz.app.design_system.model.StoryUi


@Suppress("NonSkippableComposable")
@Composable
fun HomeStories(
    modifier: Modifier = Modifier,
    stories: List<StoryUi>,
    onStoryClick: (StoryUi) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
    ) {
        stories.forEach { story ->
            HomeStoryCard(
                storyImage = story.image,
                viewed = true,
                onClick = {
                    onStoryClick(story)
                }
            )
        }
    }
}

@Composable
fun HomeStoryCard(
    modifier: Modifier = Modifier,
    storyImage: String,
    viewed: Boolean,
    onClick: () -> Unit,
) {
    VodovozOutlinedCard(
        modifier = modifier.size(96.dp),
        contentPadding = PaddingValues(2.dp),
        shape = MaterialTheme.shapes.medium,
        borderColor = if (!viewed) MaterialTheme.colorScheme.primary else Color.Transparent,
        onClick = onClick
    ) {
        AsyncImage(
            model = storyImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
        )
    }
}