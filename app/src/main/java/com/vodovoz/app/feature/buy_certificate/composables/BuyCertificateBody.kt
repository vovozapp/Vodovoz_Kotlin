package com.vodovoz.app.feature.buy_certificate.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vodovoz.app.design_system.composables.button.VodovozButtonsColumn
import com.vodovoz.app.design_system.composables.decoration.VodovozHorizontalDivider
import com.vodovoz.app.design_system.composables.text_fields.VodovozTextFieldsColumn
import com.vodovoz.app.design_system.model.ColorfulButtonUi
import com.vodovoz.app.design_system.model.PaymentTypeUi
import com.vodovoz.app.feature.buy_certificate.model.BuyCertificateErrorsUi
import com.vodovoz.app.feature.buy_certificate.model.BuyCertificateTabUi
import com.vodovoz.app.feature.buy_certificate.model.CertificateUi
import com.vodovoz.app.feature.buy_certificate.model.FAQUi
import com.vodovoz.app.feature.preorder.model.FieldUi

@Suppress("NonSkippableComposable")
@Composable
fun BuyCertificateBody(
    modifier: Modifier = Modifier,
    certificatesTitle: String,
    certificates: List<CertificateUi>,
    currentCertificate: CertificateUi,
    currentTab: BuyCertificateTabUi,
    tabs: List<BuyCertificateTabUi>,
    paymentTitle: String,
    paymentTypes: List<PaymentTypeUi>,
    currentPaymentType: PaymentTypeUi,
    button: ColorfulButtonUi,
    faq: FAQUi,
    errors: BuyCertificateErrorsUi,
    onCertificateClick: (CertificateUi) -> Unit,
    onTabClick: (BuyCertificateTabUi) -> Unit,
    onFieldChange: (FieldUi, FieldUi) -> Unit,
    onPaymentTypeClick: (PaymentTypeUi) -> Unit,
    onFAQButtonClick: (FAQUi) -> Unit,
    onButtonClick: (ColorfulButtonUi) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CertificatesRow(
            modifier = Modifier.padding(top = 8.dp),
            title = certificatesTitle,
            certificates = certificates,
            currentCertificate = currentCertificate,
            onCertificateClick = onCertificateClick,
            error = errors.certificate
        )

        VodovozHorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        BuyCertificateTabs(
            modifier = Modifier.padding(horizontal = 16.dp),
            tabs = tabs,
            currentTab = currentTab,
            onTabClick = onTabClick
        )

        if (currentTab.fields.isNotEmpty()) {
            VodovozTextFieldsColumn(
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                fields = currentTab.fields,
                onFieldChange = onFieldChange,
                onDone = {}
            )
        }

        VodovozHorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )

        BuyCertificatePaymentColum(
            title = paymentTitle,
            paymentTypes = paymentTypes,
            error = errors.payment,
            currentPaymentType = currentPaymentType,
            onPaymentTypeClick = onPaymentTypeClick,
        )

        VodovozHorizontalDivider()

        FAQButton(
            faq = faq,
            onFAQClick = onFAQButtonClick,
            modifier = Modifier.padding(top = 2.dp)
        )

        VodovozButtonsColumn(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp,
                top = 18.dp
            ),
            buttons = listOf(button),
            onButtonClick = onButtonClick
        )
    }
}