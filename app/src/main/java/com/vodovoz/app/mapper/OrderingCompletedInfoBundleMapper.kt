package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.OrderingCompletedInfoBundleEntity
import com.vodovoz.app.ui.model.custom.OrderingCompletedInfoBundleUI

object OrderingCompletedInfoBundleMapper {

    fun OrderingCompletedInfoBundleEntity.mapToUI() = OrderingCompletedInfoBundleUI(
        orderId = orderId,
        message = message,
        paymentURL = paymentURL
    )

}