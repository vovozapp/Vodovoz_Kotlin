package com.vodovoz.app.feature.favorite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.LayoutSwitchButton
import com.vodovoz.app.design_system.composables.button.SortingButton
import com.vodovoz.app.design_system.composables.chip.VodovozChip
import com.vodovoz.app.design_system.composables.list.ProductLazyList
import com.vodovoz.app.design_system.composables.list.ProductListHeader
import com.vodovoz.app.design_system.composables.tab_row.VodovozScrollableTabRow
import com.vodovoz.app.feature.favorite.composables.FavoriteTopBar
import com.vodovoz.app.util.extensions.indexOfOrNull

@Suppress("NonSkippableComposable")
@Composable
fun FavoriteScreen(
    viewModel: FavoriteFlowViewModel,
    viewState: FavoriteFlowViewModel.FavoriteState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        FavoriteTopBar(
            onSearchClick = {

            }
        )

        val categories = viewState.categories

        VodovozScrollableTabRow(
            modifier = Modifier.padding(top = 8.dp),
            selectedTabIndex = categories.indexOfOrNull(viewState.currentCategory) ?: 0,
            edgePadding = 16.dp,
            spacing = 8.dp
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_category),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        viewModel.navigateToCategories()
                    }
            )

            categories.forEach { category ->
                VodovozChip(
                    text = category.name,
                    selected = category == viewState.currentCategory,
                    onSelect = { }
                )
            }
        }

        ProductListHeader(
            modifier = Modifier.padding(top = 24.dp),
            sortName = viewState.currentSort.name,
            isGridView = viewState.isGridView,
            onSwitchClick = {
                viewModel.showSortOptionsBottomSheet()
            },
            onSortingClick = {
                viewModel.switchLayoutView()
            }
        )

        val products = viewState.productsSection.products

        ProductLazyList(
            products = products,
            isGridView = viewState.isGridView,
            onProductClick = {},
            onProductLike = {}
        )
    }
}