package com.vodovoz.app.feature.full_screen_history_slider

import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.feature.full_screen_history_slider.composables.StoriesIndicator

enum class DragAnchor(val value: Float) {
    Top(1f),
    Center(0f),
    Bottom(-1f),
}


@OptIn(ExperimentalFoundationApi::class)
@Suppress("NonSkippableComposable")
@Composable
fun StoriesScreen(
    viewState: FullScreenHistoriesSliderFlowViewModel.HistoriesSliderState,
    viewModel: FullScreenHistoriesSliderFlowViewModel,
    pagerState: PagerState,
) {
    val stories = viewState.stories
    val systemUiController = rememberSystemUiController()

    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    DisposableEffect(Unit) {
        systemUiController.setSystemBarsColor(onBackgroundColor)
        onDispose {
            systemUiController.setSystemBarsColor(backgroundColor)
        }
    }

    val density = LocalDensity.current

    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchor.Center,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 200.dp.toPx() } },
            snapAnimationSpec = tween(300),
            decayAnimationSpec = splineBasedDecay(density),
            confirmValueChange = { true },
        ).apply {
            updateAnchors(
                DraggableAnchors {
                    DragAnchor.entries.forEach { anchor -> anchor at (anchor.value * 1500f) }
                }
            )
        }
    }






    HorizontalPager(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .background(MaterialTheme.colorScheme.onBackground)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val change = awaitFirstDown()
                        val startTime = System.currentTimeMillis()
                        viewModel.stopStory()
                        waitForUpOrCancellation()
                        if (System.currentTimeMillis() - startTime < 160) {
                            if (change.position.x < size.width / 2.5) {
                                viewModel.goPreviousStoryPage()
                            } else if (change.position.x > size.width - (size.width / 2.5)) {
                                viewModel.goNextStoryPage()
                            }
                        }
                        viewModel.resumeStory()
                    }
                }
            },
        state = pagerState,
        beyondViewportPageCount = stories.size
    ) { i ->
        val story = stories[i]
        val storyPage = story.pages.getOrNull(viewState.currentPageIndex) ?: story.pages.first()
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
//                .offset {
//                    anchoredDraggableState.offset
//
//                    IntOffset(
//                        0,
//                        y = anchoredDraggableState
//                            .requireOffset()
//                            .toInt()
//                    )
//                }
                .graphicsLayer {
                    val startOffset = pagerState.startOffsetForPage(i)
                    translationX = size.width * (startOffset * .99f)

                    alpha = (2f - startOffset) / 2f

                    val scale = 1f - (startOffset * .3f)
                    scaleX = scale
                    scaleY = scale
                }
                .clip(MaterialTheme.shapes.large)
                .anchoredDraggable(anchoredDraggableState, Orientation.Vertical)
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = storyPage.image,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                StoriesIndicator(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(top = 8.dp),
                    countPages = story.pages.size,
                    pageIndex = viewState.currentPageIndex,
                    progress = (viewState.timePassed.toFloat() / storyPage.durationMillis)
                )
                CloseButton(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp),
                    onCloseClick = {
                        viewModel.navigateBack()
                    }
                )
                Spacer(modifier = Modifier.weight(1f))

                val actionWithButton = storyPage.actionWithButton
                val colorfulButton = actionWithButton.colorfulButton

                VodovozButton(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    text = colorfulButton.name,
                    onClick = {

                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorfulButton.backgroundColor,
                        contentColor = colorfulButton.textColor
                    )
                )
            }
        }

    }
}

fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

fun PagerState.startOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtLeast(0f)
}

fun PagerState.endOffsetForPage(page: Int): Float {
    return offsetForPage(page).coerceAtMost(0f)
}

@Composable
private fun CloseButton(modifier: Modifier = Modifier, onCloseClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(32.dp)
            .clickable { onCloseClick() }
            .background(MaterialTheme.colorScheme.background.copy(0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_close_stories),
            contentDescription = null,
            modifier = Modifier.size(13.dp),
            tint = MaterialTheme.colorScheme.background
        )
    }
}