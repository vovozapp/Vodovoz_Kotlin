package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable

class AddressUI(
    var id: Long? = null,
    val type: Int? = null,
    val fullAddress: String? = null,
    val phone: String? = null,
    val name: String? = null,
    val email: String? = null,
    val locality: String? = null,
    val street: String? = null,
    val house: String? = null,
    val entrance: String? = null,
    val floor: String? = null,
    val flat: String? = null,
    val comment: String? = null
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeValue(type)
        parcel.writeString(fullAddress)
        parcel.writeString(phone)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(locality)
        parcel.writeString(street)
        parcel.writeString(house)
        parcel.writeString(entrance)
        parcel.writeString(floor)
        parcel.writeString(flat)
        parcel.writeString(comment)
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


