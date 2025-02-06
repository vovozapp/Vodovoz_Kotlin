package com.vodovoz.app.feature.full_screen_history_slider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.feature.full_screen_history_slider.composables.StoriesIndicator

@Suppress("NonSkippableComposable")
@Composable
fun StoriesScreen(
    viewState: FullScreenHistoriesSliderFlowViewModel.HistoriesSliderState,
    viewModel: FullScreenHistoriesSliderFlowViewModel,
    navController: NavController,
) {
    val stories = viewState.stories
    val pagerState = rememberPagerState(initialPage = viewState.currentStoryIndex) { stories.size }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    HorizontalPager(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        state = pagerState,
        beyondViewportPageCount = 0
    ) { i ->
        val story = stories[i]
        val storyPage = story.pages.getOrNull(viewState.currentPageIndex) ?: story.pages.first()
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val change = awaitFirstDown()
                            val startTime = System.currentTimeMillis()
                            viewModel.stopStory()
                            waitForUpOrCancellation()
                            if (System.currentTimeMillis() - startTime < 200) {
                                if (change.position.x < size.width / 2) {
                                    viewModel.goPreviousStoryPage()
                                } else {
                                    viewModel.goNextStoryPage()
                                }
                            }
                            viewModel.resumeStory()
                        }
                    }
                }
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

    LaunchedEffect(pagerState.currentPage) {
        viewModel.changeStoryIndex(pagerState.currentPage)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.observeEvent().collect { event ->
                when (event) {
                    is FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents.ChangePagerIndex -> {
                        pagerState.animateScrollToPage(event.newStoryIndex)
                    }

                    FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents.GoBack -> {
                        navController.popBackStack()
                    }

                    FullScreenHistoriesSliderFlowViewModel.HistoriesSliderEvents.GoToProfile -> {

                    }
                }
            }
        }
    }
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