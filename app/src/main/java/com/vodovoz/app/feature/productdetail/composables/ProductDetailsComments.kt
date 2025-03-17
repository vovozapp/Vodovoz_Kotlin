package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults
import com.vodovoz.app.design_system.composables.card.CommentCard
import com.vodovoz.app.design_system.model.ButtonUi
import com.vodovoz.app.design_system.model.CommentUi
import com.vodovoz.app.feature.home.composables.TitleAndButton

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsComments(
    modifier: Modifier = Modifier,
    commentsCount: Int,
    comments: List<CommentUi>,
    onWriteCommentClick: () -> Unit,
    onShowAllCommentsClick: () -> Unit,
) {
    Column(modifier = modifier) {

        TitleAndButton(
            title = stringResource(id = R.string.comments),
            button = if (commentsCount > 0) ButtonUi.Empty else null,
            onShowAllClick = { onShowAllCommentsClick() })


        if (commentsCount > 0) {
            CommentsPager(modifier = Modifier.padding(top = 24.dp), comments = comments)
        } else {
            Text(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp),
                text = stringResource(R.string.no_comments),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        VodovozButton(
            modifier = Modifier
                .padding(top = 18.dp)
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.leave_rate),
            onClick = onWriteCommentClick,
            colors = VodovozButtonDefaults.secondaryColors()
        )


    }
}

@Suppress("NonSkippableComposable")
@Composable
private fun CommentsPager(
    modifier: Modifier = Modifier,
    comments: List<CommentUi>,
) {
    val pagerState = rememberPagerState { comments.size }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 8.dp,
        beyondViewportPageCount = comments.size,
    ) { page ->
        CommentCard(
            comment = comments[page],
            maxLines = 2
        )
    }
}