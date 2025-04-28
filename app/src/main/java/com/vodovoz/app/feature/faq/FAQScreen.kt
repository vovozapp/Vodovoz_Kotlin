package com.vodovoz.app.feature.faq

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.faq.composables.FAQBody
import com.vodovoz.app.feature.faq.model.FAQState

@Composable
fun FAQScreen(viewModel: FAQViewModel, viewState: FAQState) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        VodovozTopBar(
            onBack = { viewModel.navigateBack() },
            title = viewState.name
        )
        FAQBody(
            faqItems = viewState.items,
            onFAQItemClick = { faqItem ->
                viewModel.changeExpand(faqItem)
            }
        )
    }
}