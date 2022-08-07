package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable

class AddressUI(
    var id: Long,
    val type: Int,
    val fullAddress: String,
    val phone: String,
    val name: String,
    val email: String,
    val locality: String,
    val street: String,
    val house: String,
    val entrance: String,
    val floor: String,
    val flat: String,
    val comment: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "") {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(type)
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


