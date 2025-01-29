package com.vodovoz.app.feature.productdetail.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.VodovozTheme
import java.util.Locale

@Composable
fun ProductDetailsRatingBar(
    modifier: Modifier = Modifier,
    rating: String,
    numberOfReviews: Int,
    articleNumber: String,
    onCopyClick: () -> Unit,
    onReviewsClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rating,
            color = if (numberOfReviews == 0) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_star),
            contentDescription = null,
            tint = if (numberOfReviews == 0) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(16.dp),
        )

        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .clickable(
                    null,
                    null,
                    onClick = onReviewsClick
                ),
            text = if (numberOfReviews == 0) stringResource(R.string.leave_feedback) else pluralStringResource(
                id = R.plurals.reviews_count,
                numberOfReviews,
                numberOfReviews
            ),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .clickable(onClick = onCopyClick)
        ) {
            Text(
                text = stringResource(R.string.article_number, articleNumber),
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.labelSmall
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_copy),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceTint,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(18.dp),
            )
        }


    }
}

@Preview
@Composable
private fun ProductDetailsRatingBarPreview() {
    VodovozTheme {
        ProductDetailsRatingBar(
            rating = "4.3",
            numberOfReviews = 0,
            articleNumber = "123456789",
            onReviewsClick = {},
            onCopyClick = {},
        )
    }
}