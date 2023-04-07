package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressUI(
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
): Parcelable


