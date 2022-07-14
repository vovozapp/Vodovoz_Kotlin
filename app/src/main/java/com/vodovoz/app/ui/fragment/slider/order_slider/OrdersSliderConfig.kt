package com.vodovoz.app.ui.fragment.slider.order_slider

import android.os.Parcel
import android.os.Parcelable

class OrdersSliderConfig(
    val containTitleContainer: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (containTitleContainer) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrdersSliderConfig> {
        override fun createFromParcel(parcel: Parcel): OrdersSliderConfig {
            return OrdersSliderConfig(parcel)
        }

        override fun newArray(size: Int): Array<OrdersSliderConfig?> {
            return arrayOfNulls(size)
        }
    }
}