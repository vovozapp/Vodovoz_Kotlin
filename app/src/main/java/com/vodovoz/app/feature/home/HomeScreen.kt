package com.vodovoz.app.feature.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.vodovoz.app.design_system.composables.top_bar.SearchTopBar
import com.vodovoz.app.feature.all.promotions.AllPromotionsFragment
import com.vodovoz.app.feature.home.composables.HomeBody

@Suppress("NonSkippableComposable")
@Composable
fun HomeScreen(
    viewState: HomeFlowViewModel.HomeState,
    viewModel: HomeFlowViewModel,
    navController: NavController,
) {

    Scaffold(
        topBar = {
            SearchTopBar(
                value = viewState.searchField,
                onValueChange = { },
                onFocus = { },
                onMicClick = { },
                onScanClick = { },
                onSearchClick = { }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        HomeBody(
            modifier = Modifier.padding(paddingValues),
            banners = viewState.banners,
            promotions = viewState.promotions,
            orderWithMenu = viewState.orderWithMenu,
            popularSections = viewState.popularSections,
            newProducts = viewState.newProducts,
            hurryUpBuyProducts = viewState.hurryUpBuyProducts,
            bestOffersSection = viewState.bestOffersSection,
            bottomSection = viewState.bottomSection,
            currentCategoryWithProducts = viewState.currentCategoryWithProducts,
            onCategorySelect = { categoryWithProductsUi ->
                viewModel.selectCategory(categoryWithProductsUi)
            },
            onMenuItemClick = { },
            onOrderClick = { },
            onPopularSectionClick = { },
            onShowAllPromotionClick = {
                navController.navigate(
                    HomeFragmentDirections.actionToAllPromotionsFragment(
                        AllPromotionsFragment.DataSource.All
                    )
                )
            }
        )
    }
}