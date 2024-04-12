package com.vodovoz.app.ui.model.custom

import android.os.Parcelable
import com.vodovoz.app.ui.model.OrderFilterUI
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrdersFiltersBundleUI(
    var orderId: Long? = null,
    var orderFilterUIList: MutableList<OrderFilterUI> = mutableListOf(),
) : Parcelable