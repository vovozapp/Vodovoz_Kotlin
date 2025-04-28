package com.vodovoz.app.feature.buy_certificate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vodovoz.app.design_system.composables.snackbar.VodovozSnackbarHost
import com.vodovoz.app.design_system.composables.top_bar.VodovozTopBar
import com.vodovoz.app.feature.buy_certificate.composables.BuyCertificateBody

@Suppress("NonSkippableComposable")
@Composable
fun BuyCertificateScreen(
    viewModel: BuyCertificateViewModel,
    viewState: BuyCertificateViewModel.BuyCertificateState,
    snackbarHostState: SnackbarHostState
) {
    Column(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        VodovozTopBar(
            onBack = { viewModel.navigateBack() },
            title = viewState.title
        )
        BuyCertificateBody(
            modifier = Modifier.weight(1f),
            certificates = viewState.certificates,
            certificatesTitle = viewState.certificatesTitle,
            currentCertificate = viewState.currentCertificate,
            currentTab = viewState.currentTab,
            tabs = viewState.tabs,
            paymentTitle = viewState.paymentTitle,
            paymentTypes = viewState.paymentTypes,
            currentPaymentType = viewState.currentPaymentType,
            button = viewState.button,
            faq = viewState.faq,
            errors = viewState.errors,
            onTabClick = { tab ->
                viewModel.selectTab(tab)
            },
            onCertificateClick = { certificate ->
                viewModel.selectCertificate(certificate)
            },
            onFieldChange = { field, updatedField ->
                viewModel.changeField(field, updatedField)
            },
            onPaymentTypeClick = { paymentType ->
                viewModel.selectPaymentType(paymentType)
            },
            onButtonClick = { button ->
                viewModel.activateButton(button)
            },
            onFAQButtonClick = { faqUi ->
                viewModel.navigateToFAQ(faqUi)
            }
        )
        VodovozSnackbarHost(hostState = snackbarHostState)
    }
}