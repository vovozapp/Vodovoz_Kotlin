package com.vodovoz.app.ui.model.custom

import android.os.Parcel
import android.os.Parcelable
import com.vodovoz.app.ui.model.OrderStatusUI
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrdersFiltersBundleUI(
    var orderId: Long? = null,
    var orderStatusUIList: MutableList<OrderStatusUI> = mutableListOf()
) : Parcelable