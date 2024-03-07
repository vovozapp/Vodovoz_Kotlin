package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.ui.model.PayMethodUI

data class BuyCertificatePaymentUI(
    val code: String,
    val payMethods: List<PayMethodUI>,
    val name: String,
    val required: Boolean
)