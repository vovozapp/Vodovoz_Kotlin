package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.card.CommentCard
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozButtonDefaults
import com.vodovoz.app.ui.model.CommentUI
import java.util.Random

@Suppress("NonSkippableComposable")
@Composable
fun ProductDetailsComments(
    modifier: Modifier = Modifier,
    commentsAmount: Int,
    comments: List<CommentUI>,
    onLeaveRateClick: () -> Unit,

    ) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.comments),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            if (commentsAmount > 0) {
                Row(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null
                    ) { onLeaveRateClick() }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = stringResource(id = R.string.all),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(24.dp)
                    )
                }
            }
        }

        if (commentsAmount > 0) {
            CommentsPager(modifier = Modifier.padding(top = 24.dp), commentsUI = comments)
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
            onClick = onLeaveRateClick,
            colors = VodovozButtonDefaults.secondaryColors()
        )


    }
}

@Suppress("NonSkippableComposable")
@Composable
private fun CommentsPager(
    modifier: Modifier = Modifier,
    commentsUI: List<CommentUI>,
) {
    val pagerState = rememberPagerState { commentsUI.count() }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        pageSpacing = 8.dp,
        beyondViewportPageCount = 3,
        key = {
            commentsUI[it].id ?: Random().nextInt()
        }
    ) { page ->
        CommentCard(
            commentUI = commentsUI[page]
        )
    }
}