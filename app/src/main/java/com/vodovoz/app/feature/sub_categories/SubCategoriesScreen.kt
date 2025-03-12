package com.vodovoz.app.feature.sub_categories

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.top_bar.StaticSearchTopBar
import com.vodovoz.app.feature.sub_categories.composables.SubCategoriesBody
import com.vodovoz.app.feature.sub_categories.model.SubCategoriesState

@Composable
fun SubCategoriesScreen(
    viewModel: SubCategoriesViewModel,
    viewState: SubCategoriesState,
) {
    Scaffold(
        topBar = {
            StaticSearchTopBar(
                onFocus = {
                    viewModel.navigateToSearch()
                },
                onMicClick = {

                },
                onScanClick = {

                },
                onNavigationClick = {
                    viewModel.navigateBack()
                }
            )
        },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { paddingValues ->
        SubCategoriesBody(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            catalogCategory = viewState.catalogCategory,
            onCategoryClick = { catalogCategory ->
                viewModel.chooseCatalogCategory(catalogCategory)
            },
            onParentCategoryClick = { catalogCategory ->
                viewModel.chooseParentCatalogCategory(catalogCategory)
            }
        )
    }
}