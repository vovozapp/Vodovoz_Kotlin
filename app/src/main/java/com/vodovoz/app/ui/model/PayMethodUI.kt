package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable

class PayMethodUI(
    val id: Long = 0,
    val name: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "") {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PayMethodUI> {
        override fun createFromParcel(parcel: Parcel): PayMethodUI {
            return PayMethodUI(parcel)
        }

        override fun newArray(size: Int): Array<PayMethodUI?> {
            return arrayOfNulls(size)
        }
    }
}