package com.vodovoz.app.feature.product_comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.card.CommentCard
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.floating.BottomFloatingContainer
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.tab_row.VodovozScrollableTabRow
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.product_comments.model.ProductCommentsInfoUi
import com.vodovoz.app.util.extensions.indexOfOrNull

@Suppress("NonSkippableComposable")
@Composable
fun ProductCommentsScreen(
    viewModel: ProductCommentsFlowViewModel,
    viewState: ProductCommentsFlowViewModel.ProductCommentsState,
    lazyListState: LazyListState,
) {

    val aboutComments = viewState.productCommentsInfo


    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        VodovozTopBar(
            onBack = {
                viewModel.navigateBack()
            },
            title = stringResource(id = R.string.comments)
        )

        val sorting = aboutComments.sorting
        if (sorting.isNotEmpty()) {
            VodovozScrollableTabRow(
                modifier = Modifier.padding(top = 8.dp),
                selectedTabIndex = sorting.indexOfOrNull(viewState.currentSort) ?: 0,
                spacing = 8.dp,
                edgePadding = 16.dp
            ) {
                sorting.forEach { sort ->
                    VodovozChip(
                        text = sort.name,
                        selected = sort == viewState.currentSort,
                        onSelect = { viewModel.selectSort(sort) }
                    )
                }
            }
        }

        val lazyPagingComments = viewState.pagedComments.collectAsLazyPagingItems()
        val loadState = lazyPagingComments.loadState

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = lazyListState
        ) {

            item {
                CommentsInfoCard(
                    aboutComments = aboutComments,
                    modifier = Modifier
                )
            }

            if (loadState.refresh is LoadState.Loading) {
                item {
                    LoadingPlaceholder()
                }
            } else {

                items(
                    count = lazyPagingComments.itemCount
                ) { i ->
                    val comment = lazyPagingComments[i]
                    if (comment != null) {
                        CommentCard(comment = comment, minLines = 1)
                    }
                }
            }
            if (loadState.append is LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(align = Alignment.Center)
                            .size(30.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        AnimatedVisibility(viewState.showWriteComment) {
            BottomFloatingContainer {
                VodovozButton(
                    text = stringResource(id = R.string.write_comment_btn_text),
                    onClick = { viewModel.navigateToWriteComment() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun CommentsInfoCard(modifier: Modifier = Modifier, aboutComments: ProductCommentsInfoUi) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(
                    R.string.rating_value,
                    aboutComments.ratingText
                ),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(18.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = aboutComments.commentsCountText,
            color = MaterialTheme.colorScheme.surfaceTint,
            style = MaterialTheme.typography.bodySmall
        )
    }
}