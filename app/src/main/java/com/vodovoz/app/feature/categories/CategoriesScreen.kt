package com.vodovoz.app.feature.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.button.VodovozButton
import com.vodovoz.app.design_system.composables.button.VodovozRadioRow
import com.vodovoz.app.design_system.composables.floating.BottomFloatingContainer
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.categories.model.CategoriesState

@Suppress("NonSkippableComposable")
@Composable
fun CategoriesScreen(viewModel: CategoriesViewModel, viewState: CategoriesState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .consumeWindowInsets(WindowInsets.systemBars)
            .background(MaterialTheme.colorScheme.background)
    ) {
        VodovozTopBar(
            onBack = { viewModel.navigateBack() },
            title = stringResource(id = R.string.categories)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            viewState.categories.forEachIndexed { index, category ->
                VodovozRadioRow(
                    name = category.name,
                    selected = category == viewState.currentCategory,
                    onSelect = { viewModel.selectCategory(category) })

                if (viewState.categories.lastIndex != index) {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surface)
                }
            }

        }

        BottomFloatingContainer {
            VodovozButton(
                text = stringResource(R.string.apply),
                onClick = { viewModel.navigateBackWithArgs() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}