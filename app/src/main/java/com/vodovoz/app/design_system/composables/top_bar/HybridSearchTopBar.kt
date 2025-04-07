package com.vodovoz.app.design_system.composables.top_bar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.ClickableIcon

@Composable
fun HybridSearchTopBar(
    modifier: Modifier = Modifier,
    title: String,
    searchQuery: String,
    isSearchMode: Boolean,
    onSearchModeChange: (Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNavigationClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            ClickableIcon(
                modifier = Modifier.clip(CircleShape),
                painter = painterResource(id = R.drawable.ic_arrow_left),
                tint = MaterialTheme.colorScheme.onBackground,
                onClick = onNavigationClick
            )

            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = isSearchMode,
                label = "AnimatedTitleSearch",
                transitionSpec = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right).togetherWith(
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                    )
                }) { targetState ->
                if (targetState) {
                    val focusRequester = remember {
                        FocusRequester()
                    }

                    BasicSearchField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .focusRequester(focusRequester),
                        value = searchQuery,
                        onValueChange = { s -> onSearchQueryChange(s) },
                        onSearchClick = { focusRequester.freeFocus() },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.search_),
                                        color = MaterialTheme.colorScheme.surfaceTint,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    LaunchedEffect(focusRequester) {
                        focusRequester.requestFocus()
                    }
                } else {
                    Text(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 24.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null,
                                onClick = { onSearchModeChange(!isSearchMode) }
                            ),
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }


            ClickableIcon(
                modifier = Modifier.clip(CircleShape),
                painter = painterResource(id = if (!isSearchMode) R.drawable.icon_search else R.drawable.ic_clean),
                tint = if (!isSearchMode) MaterialTheme.colorScheme.onBackground
                    else if (searchQuery.isBlank()) Color.Transparent
                    else MaterialTheme.colorScheme.surfaceTint,
                onClick = { onSearchModeChange(!isSearchMode) }
            )
        }

    }

}