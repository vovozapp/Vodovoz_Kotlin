package com.vodovoz.app.ui.model.custom

import android.os.Parcel
import android.os.Parcelable
import com.vodovoz.app.ui.model.OrderStatusUI

class OrdersFiltersBundleUI(
    var orderId: Long? = null,
    var orderStatusUIList: MutableList<OrderStatusUI> = mutableListOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        mutableListOf<OrderStatusUI>().apply {
            parcel.createStringArray()?.forEach { add(OrderStatusUI.fromId(it)) }
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(orderId)
        parcel.writeStringArray(mutableListOf<String>().apply {
            orderStatusUIList.forEach { add(it.name) }
        }.toTypedArray())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrdersFiltersBundleUI> {
        override fun createFromParcel(parcel: Parcel): OrdersFiltersBundleUI {
            return OrdersFiltersBundleUI(parcel)
        }

        override fun newArray(size: Int): Array<OrdersFiltersBundleUI?> {
            return arrayOfNulls(size)
        }
    }
}