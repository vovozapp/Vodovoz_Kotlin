package com.vodovoz.app.feature.all.brands

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.placeholders.LoadingPlaceholder
import com.vodovoz.app.design_system.composables.top_bar.HybridSearchTopBar
import com.vodovoz.app.design_system.model.BrandUi

@Suppress("NonSkippableComposable")
@Composable
fun AllBrandsScreen(
    viewModel: AllBrandsFlowViewModel,
    viewState: AllBrandsFlowViewModel.AllBrandsState,
) {
    val lazyPagingBrands = viewState.brands.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        HybridSearchTopBar(
            title = viewState.title,
            searchQuery = viewState.searchQuery,
            isSearchMode = viewState.isSearchMode,
            onSearchModeChange = { searchMode ->
                viewModel.changeSearchMode(searchMode)
            },
            onSearchQueryChange = { newSearchQuery ->
                viewModel.changeSearchQuery(newSearchQuery)
            },
            onNavigationClick = {
                viewModel.navigateBack()
            }
        )


        val loadState = lazyPagingBrands.loadState

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {

            when (loadState.refresh) {
                is LoadState.Error -> {
                    item {
                        LaunchedEffect(Unit) {
                            viewModel.navigateBack()
                        }
                    }
                }

                LoadState.Loading -> {
                    item { LoadingPlaceholder(modifier = Modifier.fillParentMaxSize()) }
                }

                is LoadState.NotLoading -> {
                    items(
                        count = lazyPagingBrands.itemCount,
                        key = { index -> lazyPagingBrands[index]?.id ?: -index }
                    ) { index ->
                        val brand = lazyPagingBrands[index] ?: return@items
                        Column(modifier = Modifier.animateItem(placementSpec = null)) {
                            if (index == 0) {
                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                            BrandItem(
                                brand = brand,
                                onBrandClick = { viewModel.navigateToBrandProducts(brand.id) }
                            )
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }

                }
            }

            when (loadState.append) {
                LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier
                                .height(60.dp)
                                .fillParentMaxWidth()
                                .wrapContentSize(Alignment.Center)
                                .size(30.dp)
                        )
                    }
                }

                else -> {}
            }
        }
    }
}


@Composable
private fun BrandItem(
    modifier: Modifier = Modifier,
    brand: BrandUi,
    onBrandClick: (BrandUi) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onBrandClick(brand) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            text = brand.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.surfaceTint
        )
    }
}