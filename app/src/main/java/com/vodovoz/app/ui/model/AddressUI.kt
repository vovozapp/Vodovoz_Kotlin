package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable
import com.vodovoz.app.data.model.common.AddressEntity

class AddressUI(
    val locality: String? = null,
    val street: String? = null,
    val house: String? = null,
    val fullAddress: String? = null,
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(locality)
        parcel.writeString(street)
        parcel.writeString(house)
        parcel.writeString(fullAddress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddressUI> {
        override fun createFromParcel(parcel: Parcel): AddressUI {
            return AddressUI(parcel)
        }

        override fun newArray(size: Int): Array<AddressUI?> {
            return arrayOfNulls(size)
        }
    }
}