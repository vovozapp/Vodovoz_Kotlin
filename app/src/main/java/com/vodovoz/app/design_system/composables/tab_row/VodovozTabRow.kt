package com.vodovoz.app.design_system.composables.tab_row

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.VodovozTheme
import kotlinx.coroutines.delay

@Composable
fun VodovozTabRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(4.dp),
    tabSpacing: Dp = 4.dp,
    selectedTabPosition: Int = 0,
    animationSpec: AnimationSpec<Dp> = tween(durationMillis = 300, easing = LinearEasing),
    tabItems: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        SubcomposeLayout(
            Modifier
                .padding(contentPadding)
                .selectableGroup()
        ) { constraints ->
            val preMeasured = subcompose("PreCalculate", tabItems).map { measurable ->
                measurable.measure(constraints.copy(minWidth = 0))
            }
            val tabsCount = preMeasured.size
            if (tabsCount == 0) {
                layout(constraints.maxWidth, 0) {}
            }

            val maxIntrinsicWidth = preMeasured.maxOf { it.width }
            val maxItemHeight = preMeasured.maxOf { it.height }
            val spacingPx = tabSpacing.roundToPx()

            val totalSpacing = if (tabsCount > 1) (tabsCount - 1) * spacingPx else 0

            val totalMinWidth = tabsCount * maxIntrinsicWidth + totalSpacing

            val finalTabWidth = if (totalMinWidth > constraints.maxWidth) {
                (constraints.maxWidth - totalSpacing) / tabsCount
            } else {
                maxIntrinsicWidth + (constraints.maxWidth - totalMinWidth) / tabsCount
            }

            val tabPositions = List(tabsCount) { index ->
                val x = index * (finalTabWidth + spacingPx)
                TabPosition(x.toDp(), finalTabWidth.toDp(), contentWidth = Dp.Unspecified)
            }

            val tabPlaceables = subcompose("Tabs", tabItems).map { measurable ->
                measurable.measure(
                    constraints.copy(
                        minWidth = finalTabWidth,
                        maxWidth = finalTabWidth,
                        minHeight = maxItemHeight,
                        maxHeight = maxItemHeight
                    )
                )
            }

            val layoutWidth = constraints.maxWidth

            layout(layoutWidth, maxItemHeight) {
                subcompose("Indicator") {
                    Box(
                        Modifier
                            .tabIndicator(tabPositions[selectedTabPosition], animationSpec)
                            .fillMaxWidth()
                            .height(maxItemHeight.toDp())
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = MaterialTheme.shapes.medium
                            )
                    )
                }.forEach { measurable ->
                    measurable.measure(Constraints.fixed(layoutWidth, maxItemHeight))
                        .place(0, 0)
                }

                var xPos = 0
                tabPlaceables.forEachIndexed { index, placeable ->
                    placeable.place(x = xPos, y = 0)
                    xPos += finalTabWidth + spacingPx
                }
            }
        }
    }
}

fun Modifier.tabIndicator(
    tabPosition: TabPosition,
    animationSpec: AnimationSpec<Dp>,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = tabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabPosition.width,
        animationSpec = animationSpec, label = "currentTabWidth"
    )
    val indicatorOffset by animateDpAsState(
        targetValue = tabPosition.left,
        animationSpec = animationSpec, label = "indicatorOffset"
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset { IntOffset(indicatorOffset.roundToPx(), 0) }
        .width(currentTabWidth)
        .fillMaxHeight()
}

@Composable
fun TabTitle(
    title: String,
    position: Int,
    selected: Boolean,
    onClick: (Int) -> Unit,
) {
    Text(
        text = title,
        Modifier
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(vertical = 6.dp, horizontal = 9.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick(position) },
        color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surfaceTint,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}


@Preview
@Composable
private fun TabView() {

    VodovozTheme {

        var selectedTabPosition by remember { mutableIntStateOf(0) }

        val items = listOf(
            "Описание", "Характеристики", "Документы",
        )

        val sequence = listOf(2, 1, 0)
        var index = 0
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                selectedTabPosition = sequence[index]
                index += 1
                if (index >= 3) {
                    index = 0
                }
            }
        }

        VodovozTabRow(
            modifier = Modifier.padding(horizontal = 30.dp),
            selectedTabPosition = selectedTabPosition
        ) {
            items.forEachIndexed { index, s ->
                TabTitle(
                    title = s,
                    position = index,
                    selected = selectedTabPosition == index
                ) { selectedTabPosition = index }
            }
        }
    }
}
