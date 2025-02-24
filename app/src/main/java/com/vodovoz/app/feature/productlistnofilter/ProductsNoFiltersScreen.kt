package com.vodovoz.app.feature.productlistnofilter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.vodovoz.app.design_system.VodovozTheme
import com.vodovoz.app.design_system.composables.bottom_sheet.SortOptionsBottomSheet
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.placeholders.NetworkErrorPlaceholder
import com.vodovoz.app.design_system.composables.top_bar.SearchTopBar
import com.vodovoz.app.feature.productlistnofilter.composables.ProductsNoFilterBody

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("NonSkippableComposable")
@Composable
fun ProductsNoFiltersScreen(
    viewModel: ProductsListNoFilterFlowViewModel,
    viewState: ProductsListNoFilterFlowViewModel.ProductListNoFilterState,
) {
    val productsSection = viewState.productsSection

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
    ) {
        SearchTopBar(
            value = "",
            onValueChange = {},
            onFocus = { },
            onMicClick = { },
            onScanClick = { },
            onSearchClick = { }
        )

        val lazyPagingProducts = viewState.pagedProducts.collectAsLazyPagingItems()

        when (viewState.uiState) {
            ProductsListNoFilterFlowViewModel.UiState.Error -> {
                NetworkErrorPlaceholder {

                }
            }

            ProductsListNoFilterFlowViewModel.UiState.Loading -> {
                LoadingPlaceholder()
            }

            ProductsListNoFilterFlowViewModel.UiState.Success -> {
                ProductsNoFilterBody(
                    title = productsSection.title,
                    productsQuantity = productsSection.productsQuantity,
                    categories = productsSection.categories,
                    currentCategory = viewState.currentCategory,
                    currentSort = viewState.currentSort,
                    lazyPagingProducts = lazyPagingProducts,
                    isGridView = viewState.isGridView,
                    onSortingClick = {
                        viewModel.showSortBottomSheet()
                    },
                    onSwitchLayoutClick = {
                        viewModel.switchLayout()
                    },
                    onCategoryClick = { category ->
                        viewModel.selectCategory(category)
                    }
                )
            }
        }

    }

    if (viewState.showSortBottomSheet) {
        SortOptionsBottomSheet(
            onDismissRequest = { viewModel.hideSortBottomSheet() },
            currentSort = viewState.currentSort,
            sorting = viewState.productsSection.sorting,
            onSortSelect = { sort -> viewModel.selectSort(sort) }
        )
    }

}


//TODO()
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Test(modifier: Modifier = Modifier) {
    VodovozTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val appBarMaxHeightPx = with(LocalDensity.current) { 64.dp.roundToPx() }
            val connection = remember(appBarMaxHeightPx) {
                CollapsingItemNestedScrollConnection(appBarMaxHeightPx)
            }
            val density = LocalDensity.current
            val spaceHeight by remember(density) {
                derivedStateOf {
                    with(density) {
                        (appBarMaxHeightPx + connection.appBarOffset).toDp()
                    }
                }
            }

            Box(Modifier.nestedScroll(connection)) {
                Column {
                    Spacer(
                        Modifier
                            .padding(4.dp)
                            .height(spaceHeight)
                    )
                    LazyColumn {
                        items(33) {
                            ListItem(headlineContent = { Text(it.toString()) })
                        }
                    }
                }

                TopAppBar(
                    modifier = Modifier.offset { IntOffset(0, connection.appBarOffset) },
                    title = { Text(text = "Jetpack Compose") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White
                    )
                )
            }
        }
    }
}

//TODO()
private class CollapsingItemNestedScrollConnection(
    val itemMaxHeight: Int,
) : NestedScrollConnection {

    var appBarOffset: Int by mutableIntStateOf(0)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y.toInt()
        val newOffset = appBarOffset + delta
        val previousOffset = appBarOffset
        appBarOffset = newOffset.coerceIn(-itemMaxHeight, 0)
        val consumed = appBarOffset - previousOffset
        return Offset(0f, consumed.toFloat())
    }
}