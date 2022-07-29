package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable

class ServiceUI(
    val name: String,
    val detail: String? = null,
    val price: String? = null,
    val detailPicture: String? = null,
    val type: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(detail)
        parcel.writeString(price)
        parcel.writeString(detailPicture)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServiceUI> {
        override fun createFromParcel(parcel: Parcel): ServiceUI {
            return ServiceUI(parcel)
        }

        override fun newArray(size: Int): Array<ServiceUI?> {
            return arrayOfNulls(size)
        }
    }
}