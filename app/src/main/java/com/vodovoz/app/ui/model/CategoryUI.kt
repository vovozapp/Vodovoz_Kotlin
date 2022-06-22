package com.vodovoz.app.ui.model

import android.os.Parcel
import android.os.Parcelable


class CategoryUI(
    val id: Long? = null,
    val name: String,
    val productAmount: String? = null,
    val detailPicture: String? = null,
    var isOpen: Boolean = false,
    val primaryFilterName: String? = null,
    var primaryFilterValueList: List<FilterValueUI> = listOf(),
    var categoryUIList: List<CategoryUI> = listOf()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.createTypedArrayList(FilterValueUI)!!,
        parcel.createTypedArrayList(CREATOR)!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(productAmount)
        parcel.writeString(detailPicture)
        parcel.writeByte(if (isOpen) 1 else 0)
        parcel.writeString(primaryFilterName)
        parcel.writeTypedList(primaryFilterValueList)
        parcel.writeTypedList(categoryUIList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryUI> {
        override fun createFromParcel(parcel: Parcel): CategoryUI {
            return CategoryUI(parcel)
        }

        override fun newArray(size: Int): Array<CategoryUI?> {
            return arrayOfNulls(size)
        }
    }
}