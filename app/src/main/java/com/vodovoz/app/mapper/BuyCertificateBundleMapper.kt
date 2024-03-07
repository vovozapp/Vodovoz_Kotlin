package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.BuyCertificatePayment
import com.vodovoz.app.data.model.common.BuyCertificatePropertyEntity
import com.vodovoz.app.data.model.features.BuyCertificateBundleEntity
import com.vodovoz.app.mapper.PayMethodMapper.mapToUI
import com.vodovoz.app.ui.model.custom.BuyCertificateBundleUI
import com.vodovoz.app.ui.model.custom.BuyCertificateFieldUI
import com.vodovoz.app.ui.model.custom.BuyCertificatePaymentUI
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI

object BuyCertificateBundleMapper {

    fun BuyCertificateBundleEntity.mapToUI() = BuyCertificateBundleUI(
        title = title,
        payment = payment.mapToUI(),
        buyCertificatePropertyUIList = buyCertificatePropertyEntityList.mapToUI()
    )

    private fun BuyCertificatePayment.mapToUI() = BuyCertificatePaymentUI(
        code = code,
        payMethods = payMethods.mapToUI(),
        name = name,
        required = required == "Y"
    )

    private fun List<BuyCertificatePropertyEntity>.mapToUI() = map {
        BuyCertificatePropertyUI(
            code = it.code,
            name = it.name,
            required = it.required == "Y",
            text = it.text,
            value = it.value,
            buyCertificateFieldUIList = it.buyCertificateFields?.map { field ->
                BuyCertificateFieldUI(
                    id = field.id,
                    name = field.name
                )
            },
            showAmount = it.showAmount
        )
    }
}