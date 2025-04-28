package com.vodovoz.app.feature.faq.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.feature.buy_certificate.model.FAQItemUi

@Suppress("NonSkippableComposable")
@Composable
fun FAQBody(
    modifier: Modifier = Modifier,
    faqItems: List<FAQItemUi>,
    onFAQItemClick: (FAQItemUi) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        faqItems.forEachIndexed { index, faqItemUi ->
            key(faqItemUi.name) {
                FAQItemCard(faqItem = faqItemUi, onClick = onFAQItemClick)
            }

            if (faqItems.lastIndex != index) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FAQItemCard(
    modifier: Modifier = Modifier,
    faqItem: FAQItemUi,
    onClick: (FAQItemUi) -> Unit,
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (faqItem.expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = ""
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick(faqItem) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AnnotatedString.fromHtml(faqItem.name),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationAngle),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }


        AnimatedVisibility(
            visible = faqItem.expanded,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight / 4 },
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 200)
            ),
            exit = slideOutVertically(
                targetOffsetY = { 0 },
                animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 150)
            )
        ) {
            SelectionContainer {
                Text(
                    text = AnnotatedString.fromHtml(faqItem.description),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}