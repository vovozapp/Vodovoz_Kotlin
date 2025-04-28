package com.vodovoz.app.design_system.model

import androidx.compose.runtime.Immutable
import com.vodovoz.app.domain.general.model.certificate.PaymentTypeModel

@Immutable
data class PaymentTypeUi(
    val id: Int,
    val image: String,
    val name: String,
) {
    companion object {
        val Empty = PaymentTypeUi(0, "", "")
    }
}

fun List<PaymentTypeModel>.mapToUi(): List<PaymentTypeUi> {
    return map { it.toUi() }
}

fun PaymentTypeModel.toUi(): PaymentTypeUi {
    return PaymentTypeUi(
        id, image, name
    )
}
