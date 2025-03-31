package com.vodovoz.app.feature.catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.feature.all.promotions.composables.AdvertisingInfoBottomSheet
import com.vodovoz.app.feature.catalog.composables.CatalogBody
import com.vodovoz.app.feature.catalog.composables.CatalogLoadingPlaceholder
import com.vodovoz.app.feature.home.composables.HomeTopBar

@OptIn(ExperimentalMaterial3Api::class)
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
                    viewModel.navigateToSearch()
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            when (viewState.uiState) {
                CatalogFlowViewModel.UiState.Success -> {
                    CatalogBody(
                        categories = viewState.categories,
                        banners = viewState.banners,
                        onCategoryClick = { catalogCategory ->
                            viewModel.chooseCategory(catalogCategory)
                        },
                        onBannerClick = {

                        },
                        onAboutAdvertisingClick = {
                            viewModel.showAdvertisingBottomSheet(it)
                        }
                    )
                }

                else -> {
                    CatalogLoadingPlaceholder()
                }
            }
        }
    }

    if (viewState.showAdvertisingBS) {
        AdvertisingInfoBottomSheet(
            advertising = viewState.currentAdvertising,
            onDismissRequest = {
                viewModel.closeAdvertisingBottomSheet()
            }
        )
    }
}