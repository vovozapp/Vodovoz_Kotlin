package com.vodovoz.app.feature.all.promotions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vodovoz.app.R
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.all.promotions.composables.AllPromotionsBody

@Suppress("NonSkippableComposable")
@Composable
fun AllPromotionsScreen(
    viewModel: AllPromotionsFlowViewModel,
    viewState: AllPromotionsFlowViewModel.AllPromotionsState,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        VodovozTopBar(onBack = { /*TODO*/ }, title = stringResource(id = R.string.promotions))
        AllPromotionsBody()
    }
}