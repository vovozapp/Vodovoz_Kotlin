package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable

class FilterValueUI(
    val id: String? = null,
    val value: String,
    var isSelected: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(value)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FilterValueUI> {
        override fun createFromParcel(parcel: Parcel): FilterValueUI {
            return FilterValueUI(parcel)
        }

        override fun newArray(size: Int): Array<FilterValueUI?> {
            return arrayOfNulls(size)
        }
    }

}