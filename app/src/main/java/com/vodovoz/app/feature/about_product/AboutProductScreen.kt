package com.vodovoz.app.feature.about_product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.tab_row.TabTitle
import com.vodovoz.app.design_system.composables.tab_row.VodovozTabRow
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.about_product.model.AboutProductState

@Suppress("NonSkippableComposable")
@Composable
fun AboutProductScreen(
    viewState: AboutProductState,
    viewModel: AboutProductViewModel,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        VodovozTopBar(onBack = { /*TODO*/ }, title = stringResource(id = R.string.about_product))

        VodovozTabRow(
            modifier = Modifier.padding(top = 8.dp).padding(horizontal = 16.dp),
            selectedTabPosition = viewState.selectedTabIndex
        ) {
            viewState.tabs.forEachIndexed { i, tab ->
                TabTitle(
                    title = tab.title,
                    position = i,
                    selected = viewState.selectedTabIndex == i,
                    onClick = { viewModel.selectTab(i) }
                )
            }
        }

    }
}