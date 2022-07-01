package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable

class HistoryUI(
    val id: Long,
    val detailPicture: String,
    val bannerUIList: List<BannerUI>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.createTypedArrayList(BannerUI)!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(detailPicture)
        parcel.writeTypedList(bannerUIList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HistoryUI> {
        override fun createFromParcel(parcel: Parcel): HistoryUI {
            return HistoryUI(parcel)
        }

        override fun newArray(size: Int): Array<HistoryUI?> {
            return arrayOfNulls(size)
        }
    }
}