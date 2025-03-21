package com.vodovoz.app.feature.sub_categories

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.top_bar.VodovozSearchTopBar
import com.vodovoz.app.feature.sub_categories.composables.SubCategoriesBody
import com.vodovoz.app.feature.sub_categories.model.SubCategoriesState

@Composable
fun SubCategoriesScreen(
    viewModel: SubCategoriesViewModel,
    viewState: SubCategoriesState,
) {
    Scaffold(
        topBar = {
            VodovozSearchTopBar(
                value = viewState.searchQuery,
                readOnly = false,
                hint = stringResource(id = R.string.search_),
                onValueChange = { s ->
                    viewModel.changeSearchQuery(s)
                },
                onMicClick = {

                },
                onScanClick = {

                },
                onNavigationClick = {
                    viewModel.navigateBack()
                },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        SubCategoriesBody(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            catalogCategory = viewState.catalogCategory,
            searchQuery = viewState.searchQuery,
            onCategoryClick = { catalogCategory ->
                viewModel.chooseCatalogCategory(catalogCategory)
            },
            onParentCategoryClick = { catalogCategory ->
                viewModel.chooseParentCatalogCategory(catalogCategory)
            },
        )
    }
}