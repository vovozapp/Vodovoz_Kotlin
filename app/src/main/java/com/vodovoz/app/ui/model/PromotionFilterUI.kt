package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable

class PromotionFilterUI(
    val id: Long,
    val name: String,
    val code: String
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "") {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PromotionFilterUI> {
        override fun createFromParcel(parcel: Parcel): PromotionFilterUI {
            return PromotionFilterUI(parcel)
        }

        override fun newArray(size: Int): Array<PromotionFilterUI?> {
            return arrayOfNulls(size)
        }
    }
}