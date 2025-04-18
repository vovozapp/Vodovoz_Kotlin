package com.vodovoz.app.feature.cart.bottles.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.CartCounterButton
import com.vodovoz.app.feature.cart.bottles.model.BottleUi
import com.vodovoz.app.util.TransliterationUtils

@Suppress("NonSkippableComposable")
@Composable
fun AllBottlesBody(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    description: String,
    isSingleBottleMode: Boolean,
    searchQuery: String,
    bottles: List<BottleUi>,
    onAddBottle: (BottleUi) -> Unit,
    onDecrementBottle: (BottleUi) -> Unit,
    onIncrementBottle: (BottleUi) -> Unit,
) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = paddingValues) {

        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = description,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall
            )
        }

        itemsIndexed(
            items = bottles.filter {
                val cyrillicSearchQuery = TransliterationUtils.latinToCyrillic(searchQuery)
                it.name.contains(searchQuery, true) || it.name.contains(cyrillicSearchQuery, true)
            },
            key = { _, bottle -> bottle.id }
        ) { index, bottle ->
            Column {
                BottleItem(
                    bottle = bottle,
                    singleBottleMode = isSingleBottleMode,
                    onAddOneBottle = { bottle ->
                        onAddBottle(bottle)
                    },
                    onDecrementBottle = { bottle ->
                        onDecrementBottle(bottle)
                    },
                    onIncrementBottle = { bottle ->
                        onIncrementBottle(bottle)
                    }
                )
                if (index != bottles.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
        }
    }
}


@Composable
private fun BottleItem(
    modifier: Modifier = Modifier,
    bottle: BottleUi,
    singleBottleMode: Boolean,
    onAddOneBottle: (BottleUi) -> Unit,
    onIncrementBottle: (BottleUi) -> Unit,
    onDecrementBottle: (BottleUi) -> Unit,

    ) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = bottle.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = bottle.description,
                color = MaterialTheme.colorScheme.surfaceTint,
                style = MaterialTheme.typography.labelSmall
            )
        }


        AnimatedContent(
            targetState = bottle.cartQuantity <= 0,
            label = "AnimatedCartButtons",
            transitionSpec = {
                (fadeIn(
                    animationSpec = tween(
                        200,
                        delayMillis = 70
                    )
                ) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(200, delayMillis = 70)
                )).togetherWith(
                    fadeOut(animationSpec = tween(70)) + scaleOut(
                        targetScale = 0.92f,
                        animationSpec = tween(70)
                    )
                )
            },
            contentAlignment = Alignment.Center
        ) { targetState ->
            if (targetState) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 24.dp)
                        .size(18.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onAddOneBottle(bottle) }
                )
            } else if (!singleBottleMode) {
                CartCounterButton(
                    modifier = Modifier.padding(end = 16.dp, start = 8.dp),
                    cartQuantity = bottle.cartQuantity,
                    catalogQuantity = 10000,
                    haveTrash = false,
                    onPlusClick = {
                        onIncrementBottle(bottle)
                    },
                    onMinusClick = {
                        onDecrementBottle(bottle)
                    },
                    onTrashClick = {}
                )

            }
        }
    }
}