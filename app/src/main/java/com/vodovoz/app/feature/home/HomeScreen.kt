package com.vodovoz.app.feature.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.vodovoz.app.design_system.composables.top_bar.SearchTopBar
import com.vodovoz.app.feature.home.composables.HomeBody
import com.vodovoz.app.feature.home.composables.SpecialPromotionBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun HomeScreen(
    viewState: HomeFlowViewModel.HomeState,
    viewModel: HomeFlowViewModel,
    onNavigateToQrCodeFragment: () -> Unit,
) {

    Scaffold(
        topBar = {
            SearchTopBar(
                value = viewState.searchField,
                onValueChange = { },
                onFocus = { },
                onMicClick = { },
                onScanClick = { onNavigateToQrCodeFragment() },
                onSearchClick = { }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        HomeBody(
            modifier = Modifier.padding(paddingValues),
            banners = viewState.banners,
            stories = viewState.stories,
            sectionPromotions = viewState.sectionPromotions,
            orderWithMenu = viewState.orderWithMenu,
            sectionPopularCategories = viewState.popularSections,
            sectionNewProducts = viewState.sectionNewProducts,
            sectionHurryUpBuyProducts = viewState.sectionHurryUpBuyProducts,
            sectionTop = viewState.sectionTop,
            sectionBottomProducts = viewState.sectionBottom,
            sectionViewedProducts = viewState.sectionViewedProducts,
            currentCategoryWithProducts = viewState.currentCategoryWithProducts,
            onCategorySelect = { categoryWithProductsUi ->
                viewModel.selectCategory(categoryWithProductsUi)
            },
            onMenuItemClick = { },
            onOrderClick = { },
            onPopularSectionClick = { },
            onStoryClick = { story ->
                viewModel.navigateToStories(story)
            },
            onPromotionClick = { promotion ->
                viewModel.navigateToPromotionDetails(promotion)
            },
            onProductCardClick = { product ->
                viewModel.navigateToProductDetails(product)
            },
            onShowAllClick = { action ->
                viewModel.handleButtonAction(action)
            }
        )
    }

    if (viewState.showBottomSheet) {
        SpecialPromotionBottomSheet(
            specialPromotionUi = viewState.specialPromotionUi,
            onDismissRequest = { viewModel.closeBottomSheet() },
            onButtonClick = { }
        )
    }
}