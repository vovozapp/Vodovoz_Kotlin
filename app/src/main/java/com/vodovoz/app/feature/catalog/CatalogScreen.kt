package com.vodovoz.app.feature.catalog

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.feature.catalog.composables.CatalogBody
import com.vodovoz.app.feature.home.composables.HomeTopBar

@Suppress("NonSkippableComposable")
@Composable
fun CatalogScreen(viewModel: CatalogFlowViewModel, viewState: CatalogFlowViewModel.CatalogState) {
    Scaffold(
        topBar = {
            HomeTopBar(
                onFocus = {
                    viewModel.navigateToSearch()
                },
                onMicClick = {

                },
                onScanClick = {

                },
                onSearchClick = {

                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        CatalogBody(
            modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues),
            categories = viewState.categories,
            banners = viewState.banners,
            onCategoryClick = { catalogCategory ->
                viewModel.chooseCategory(catalogCategory)
            },
            onBannerClick = {

            }
        )
    }
}