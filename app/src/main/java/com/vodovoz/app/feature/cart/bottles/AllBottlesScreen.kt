package com.vodovoz.app.feature.cart.bottles

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.floating.BottomFloatingContainer
import com.vodovoz.app.design_system.composables.top_bar.HybridSearchTopBar
import com.vodovoz.app.feature.cart.bottles.composables.AllBottlesBody

@Suppress("NonSkippableComposable")
@Composable
fun AllBottlesScreen(
    viewModel: AllBottlesFlowViewModel,
    viewState: AllBottlesFlowViewModel.BottlesState,
) {
    val floatingButtonProgress by animateFloatAsState(
        targetValue = if (viewState.hideButton) 1f else 0f,
        animationSpec = tween(easing = LinearEasing, durationMillis = 100),
        label = "floatingButtonProgress"
    )


    Scaffold(
        topBar = {
            HybridSearchTopBar(
                title = stringResource(R.string.bottles_brand),
                searchQuery = viewState.searchQuery,
                isSearchMode = viewState.isSearchMode,
                onSearchModeChange = { searchMode ->
                    viewModel.changeSearchMode(searchMode)
                },
                onSearchQueryChange = { newQuery ->
                    viewModel.changeSearchQuery(newQuery)
                },
                onNavigationClick = {
                    viewModel.navigateBack()
                }
            )
        },
        bottomBar = {
            BottomFloatingContainer(
                modifier = Modifier.graphicsLayer {
                    translationY = lerp(0f, size.height, floatingButtonProgress)
                }
            ) {
                VodovozButton(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    text = stringResource(R.string.add_to_cart),
                    isLoading = viewState.buttonIsLoading,
                    onClick = {
                        viewModel.addBottlesToCart()
                    }
                )
            }
        }
    ) { paddingValues ->
        AllBottlesBody(
            modifier = Modifier,
            paddingValues = paddingValues,
            bottles = viewState.bottles,
            description = viewState.description,
            searchQuery = viewState.searchQuery,
            isSingleBottleMode = viewState.isSingleBottleMode,
            onDecrementBottle = { bottle ->
                viewModel.decrementBottle(bottle)
            },
            onIncrementBottle = { bottle ->
                viewModel.incrementBottle(bottle)
            },
            onAddBottle = { bottle ->
                viewModel.addBottle(bottle)
            }
        )
    }
}