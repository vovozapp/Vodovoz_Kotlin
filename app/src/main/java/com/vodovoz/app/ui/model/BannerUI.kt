package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable
import com.vodovoz.app.data.model.common.ActionEntity

class BannerUI(
    val id: Long,
    val detailPicture: String,
    val actionEntity: ActionEntity? = null
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readSerializable() as ActionEntity) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(detailPicture)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BannerUI> {
        override fun createFromParcel(parcel: Parcel): BannerUI {
            return BannerUI(parcel)
        }

        override fun newArray(size: Int): Array<BannerUI?> {
            return arrayOfNulls(size)
        }
    }
}