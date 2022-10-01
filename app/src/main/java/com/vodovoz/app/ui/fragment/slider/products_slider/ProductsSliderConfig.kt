package com.vodovoz.app.ui.fragment.slider.products_slider

import android.os.Parcel
import android.os.Parcelable

class ProductsSliderConfig(
    val containShowAllButton: Boolean,
    val largeTitle: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (containShowAllButton) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductsSliderConfig> {
        override fun createFromParcel(parcel: Parcel): ProductsSliderConfig {
            return ProductsSliderConfig(parcel)
        }

        override fun newArray(size: Int): Array<ProductsSliderConfig?> {
            return arrayOfNulls(size)
        }
    }
}