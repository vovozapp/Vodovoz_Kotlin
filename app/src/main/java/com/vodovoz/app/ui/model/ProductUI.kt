package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable

data class ProductUI(
    val id: Long,
    val name: String,
    var detailPicture: String,
    val oldPrice: Int,
    val newPrice: Int,
    val status: String,
    val statusColor: String,
    val rating: Double,
    var cartQuantity: Int,
    val isCanReturnBottles: Boolean,
    val commentAmount: String,
    val detailPictureList: List<String>
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.createStringArrayList()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(detailPicture)
        parcel.writeInt(oldPrice)
        parcel.writeInt(newPrice)
        parcel.writeString(status)
        parcel.writeString(statusColor)
        parcel.writeDouble(rating)
        parcel.writeInt(cartQuantity)
        parcel.writeByte(if (isCanReturnBottles) 1 else 0)
        parcel.writeString(commentAmount)
        parcel.writeStringList(detailPictureList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductUI> {
        override fun createFromParcel(parcel: Parcel): ProductUI {
            return ProductUI(parcel)
        }

        override fun newArray(size: Int): Array<ProductUI?> {
            return arrayOfNulls(size)
        }
    }
}