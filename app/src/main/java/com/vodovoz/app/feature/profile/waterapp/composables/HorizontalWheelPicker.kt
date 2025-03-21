package com.vodovoz.app.feature.profile.waterapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.VodovozTheme


@Composable
fun HorizontalWheelPicker(
    modifier: Modifier = Modifier,
    totalItems: Int,
    initialSelectedItem: Int,
    lineWidth: Dp = 1.5.dp,
    selectedLineHeight: Dp = 46.dp,
    multipleOfFiveLineHeight: Dp = 38.dp,
    normalLineHeight: Dp = 20.dp,
    lineSpacing: Dp = 16.dp,
    selectedLineColor: Color = MaterialTheme.colorScheme.primary,
    unselectedLineColor: Color = Color.Black.copy(0.7f),
    onItemSelected: (Int) -> Unit,
) {

    var currentSelectedItem by remember { mutableIntStateOf(initialSelectedItem) }
    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = initialSelectedItem)

    val visibleItemsInfo by remember { derivedStateOf { scrollState.layoutInfo.visibleItemsInfo } }

    val firstVisibleItemIndex = visibleItemsInfo.firstOrNull()?.index ?: -1
    val lastVisibleItemIndex = visibleItemsInfo.lastOrNull()?.index ?: -1
    val totalVisibleItems = lastVisibleItemIndex - firstVisibleItemIndex + 1
    val middleIndex = firstVisibleItemIndex + totalVisibleItems / 2
    val bufferIndices = totalVisibleItems / 2



    LaunchedEffect(currentSelectedItem) {
        onItemSelected(currentSelectedItem)
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        state = scrollState,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(lineSpacing),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(totalItems + totalVisibleItems) { index ->


            val adjustedIndex = index - bufferIndices

            if (index == middleIndex) {
                currentSelectedItem = adjustedIndex
            }

            val lineHeight = when {
                index == middleIndex -> selectedLineHeight
                adjustedIndex % 5 == 0 -> multipleOfFiveLineHeight
                else -> normalLineHeight
            }

            val textAlpha = when (index) {
                middleIndex -> 1f
                middleIndex + 5 -> 0.8f
                middleIndex - 5 -> 0.8f
                else -> 0f
            }


            val textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = textAlpha)
            val textStyle = MaterialTheme.typography.headlineSmall.copy(color = textColor)
            val textMeasurer = rememberTextMeasurer()
            val textLayoutResult = textMeasurer.measure(index.toString(), textStyle)

            Box(
                modifier = Modifier
                    .height(selectedLineHeight)
                    .drawBehind {
                        val offsetYPx = (-100).dp.toPx()

                        val centerX = size.width / 2
                        val centerY = size.height / 2 + offsetYPx

                        drawText(
                            textLayoutResult = textLayoutResult,
                            
                            topLeft = Offset(
                                x = centerX,
                                y = centerY
                            )
                        )

//                        drawText(
//                            textMeasurer = textMeasurer,
//                            text = index.toString(),
//                            style = textStyle,
//                            topLeft = Offset(
//                                x = centerX,
//                                y = centerY
//                            )
//                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                VerticalLine(
                    lineWidth = lineWidth,
                    lineHeight = lineHeight,
                    indexAtCenter = index == middleIndex,
                    selectedLineColor = selectedLineColor,
                    unselectedLineColor = unselectedLineColor
                )
            }

        }

    }
}


@Composable
private fun VerticalLine(
    lineWidth: Dp,
    lineHeight: Dp,
    indexAtCenter: Boolean,
    selectedLineColor: Color,
    unselectedLineColor: Color,
) {
    Box(
        modifier = Modifier
            .width(lineWidth)
            .height(lineHeight)
            .background(if (indexAtCenter) selectedLineColor else unselectedLineColor)
    )
}


@Preview
@Composable
private fun HorizontalWheelPickerPreview() {
    VodovozTheme {
        HorizontalWheelPicker(totalItems = 70, initialSelectedItem = 10) {}

    }
}