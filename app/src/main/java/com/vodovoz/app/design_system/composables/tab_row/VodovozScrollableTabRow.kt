package com.vodovoz.app.design_system.composables.tab_row

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.vodovoz.app.design_system.VodovozTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun VodovozScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    edgePadding: Dp = 0.dp,
    spacing: Dp = 0.dp,
    tabs: @Composable () -> Unit,
) {
    VodovozScrollableTabRowImp(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        edgePadding = edgePadding,
        spacing = spacing,
        tabs = tabs,
        scrollState = rememberScrollState()
    )
}

@Preview
@Composable
private fun VodovozTabRowPreview() {
    VodovozTheme {
        val list = remember { mutableStateListOf(1, 2, 3, 4, 5, 6) }

        VodovozScrollableTabRow(selectedTabIndex = 0) {
            list.forEach {
                AssistChip(
                    modifier = Modifier,
                    onClick = { },
                    label = { Text(it.toString()) },
                    enabled = it == list.random()
                )
            }

        }
    }
}

@Composable
private fun VodovozScrollableTabRowImp(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    edgePadding: Dp = 0.dp,
    spacing: Dp = 0.dp,
    tabs: @Composable () -> Unit,
    scrollState: ScrollState,
) {

    val coroutineScope = rememberCoroutineScope()
    val scrollableTabData = remember(scrollState, coroutineScope) {
        ScrollableTabData(
            scrollState = scrollState,
            coroutineScope = coroutineScope
        )
    }
    SubcomposeLayout(
        modifier
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.CenterStart)
            .horizontalScroll(scrollState)
            .selectableGroup()
            .clipToBounds()
    ) { constraints ->
        val minTabWidth = 0.dp.roundToPx()
        val padding = edgePadding.roundToPx()

        val tabMeasurables = subcompose(TabSlots.Tabs, tabs)


        val layoutHeight = tabMeasurables.fastFold(initial = 0) { curr, measurable ->
            maxOf(curr, measurable.minIntrinsicHeight(Constraints.Infinity))
        }


        val tabConstraints = constraints.copy(
            minWidth = minTabWidth,
            minHeight = layoutHeight,
            maxHeight = layoutHeight,
        )

        val tabPlaceables = mutableListOf<Placeable>()
        val tabContentWidths = mutableListOf<Dp>()
        tabMeasurables.fastForEach {
            val placeable = it.measure(tabConstraints)
            val contentWidth = placeable.width.toDp()
            tabPlaceables.add(placeable)
            tabContentWidths.add(contentWidth)
        }

        val spacingInPx = spacing.roundToPx()
        val layoutWidth =
            tabPlaceables.fastFold(initial = padding * 2 - spacingInPx) { curr, measurable ->
                curr + measurable.width + spacingInPx
            }

        layout(layoutWidth, layoutHeight) {
            val tabPositions = mutableListOf<TabPosition>()
            var left = padding
            tabPlaceables.fastForEachIndexed { index, placeable ->
                placeable.placeRelative(left, 0)
                tabPositions.add(
                    TabPosition(
                        left = left.toDp(),
                        width = placeable.width.toDp(),
                        contentWidth = tabContentWidths[index]
                    )
                )
                left += placeable.width + spacing.roundToPx()
            }

            scrollableTabData.onLaidOut(
                density = this@SubcomposeLayout,
                edgeOffset = padding,
                tabPositions = tabPositions,
                selectedTab = selectedTabIndex
            )
        }
    }
}


@Immutable
class TabPosition internal constructor(val left: Dp, val width: Dp, val contentWidth: Dp) {

    val right: Dp get() = left + width

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabPosition) return false

        if (left != other.left) return false
        if (width != other.width) return false
        if (contentWidth != other.contentWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + contentWidth.hashCode()
        return result
    }

    override fun toString(): String {
        return "TabPosition(left=$left, right=$right, width=$width, contentWidth=$contentWidth)"
    }

    companion object {
        val Empty = TabPosition(0.dp, 0.dp, 0.dp)
    }
}


private enum class TabSlots { Tabs }


private class ScrollableTabData(
    private val scrollState: ScrollState,
    private val coroutineScope: CoroutineScope,
) {
    private var selectedTab: Int? = null

    fun onLaidOut(
        density: Density,
        edgeOffset: Int,
        tabPositions: List<TabPosition>,
        selectedTab: Int,
    ) {
        // Animate if the new tab is different from the old tab, or this is called for the first
        // time (i.e selectedTab is `null`).
        if (this.selectedTab != selectedTab) {
            this.selectedTab = selectedTab
            tabPositions.getOrNull(selectedTab)?.let { it ->
                // Scrolls to the tab with [tabPosition], trying to place it in the center of the
                // screen or as close to the center as possible.
                val calculatedOffset = it.calculateTabOffset(density, edgeOffset, tabPositions)
                if (scrollState.value != calculatedOffset) {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(
                            calculatedOffset,
                            animationSpec = ScrollableTabRowScrollSpec
                        )
                    }
                }
            }
        }
    }


    private fun TabPosition.calculateTabOffset(
        density: Density,
        edgeOffset: Int,
        tabPositions: List<TabPosition>,
    ): Int = with(density) {
        val totalTabRowWidth = tabPositions.last().right.roundToPx() + edgeOffset
        val visibleWidth = totalTabRowWidth - scrollState.maxValue
        val tabOffset = left.roundToPx()
        val scrollerCenter = visibleWidth / 2
        val tabWidth = width.roundToPx()
        val centeredTabOffset = tabOffset - (scrollerCenter - tabWidth / 2)
        // How much space we have to scroll. If the visible width is <= to the total width, then
        // we have no space to scroll as everything is always visible.
        val availableSpace = (totalTabRowWidth - visibleWidth).coerceAtLeast(0)
        return centeredTabOffset.coerceIn(0, availableSpace)
    }
}


private val ScrollableTabRowScrollSpec: AnimationSpec<Float> = tween(
    durationMillis = 250,
    easing = FastOutSlowInEasing
)