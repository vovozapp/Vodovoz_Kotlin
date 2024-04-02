package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.BuyCertificatePayment
import com.vodovoz.app.data.model.common.BuyCertificatePropertyEntity
import com.vodovoz.app.data.model.features.BuyCertificateBundleEntity
import com.vodovoz.app.data.model.features.BuyCertificateTypeEntity
import com.vodovoz.app.mapper.PayMethodMapper.mapToUI
import com.vodovoz.app.ui.model.custom.BuyCertificateBundleUI
import com.vodovoz.app.ui.model.custom.BuyCertificateFieldUI
import com.vodovoz.app.ui.model.custom.BuyCertificatePaymentUI
import com.vodovoz.app.ui.model.custom.BuyCertificatePropertyUI
import com.vodovoz.app.ui.model.custom.BuyCertificateTypeUI

object BuyCertificateBundleMapper {

    fun BuyCertificateBundleEntity.mapToUI() = BuyCertificateBundleUI(
        title = title,
        payment = payment.mapToUI(),
        certificateInfo = certificateInfo?.mapToUI(),
        typeList = typeList?.mapTypesToUI()
    )

    private fun List<BuyCertificateTypeEntity>.mapTypesToUI(): List<BuyCertificateTypeUI> =
        map { it.mapToUI() }

    private fun BuyCertificateTypeEntity.mapToUI() = BuyCertificateTypeUI(
        type = type,
        code = code,
        name = name,
        buyCertificatePropertyList = buyCertificatePropertyEntityList?.mapPropertiesToUI()
    )

    private fun BuyCertificatePayment.mapToUI() = BuyCertificatePaymentUI(
        code = code,
        payMethods = payMethods.mapToUI(),
        name = name,
        required = required == "Y"
    )

    private fun List<BuyCertificatePropertyEntity>.mapPropertiesToUI(): List<BuyCertificatePropertyUI> = map {
        it.mapToUI()
    }

    fun BuyCertificatePropertyEntity.mapToUI() = BuyCertificatePropertyUI(
        code = code,
        name = name,
        required = required == "Y",
        text = text,
        value = value,
        buyCertificateFieldUIList = buyCertificateFields?.map { field ->
            BuyCertificateFieldUI(
                id = field.id,
                name = field.name,
                imageUrl = field.imageUrl,
            )
        },
        showAmount = showAmount
    )
}