package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.R
import com.vodovoz.app.common.content.itemadapter.Item
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
    val comment: String,
    val latitude: String = "",
    val longitude: String = "",
    val length: String = "",
    val newFullAddress: String = ""
): Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_address
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is AddressUI) return false

        return this == item
    }

}

@Parcelize
data class AddressFlowTitle(
    val title: String
): Parcelable, Item {

    override fun getItemViewType(): Int {
        return R.layout.view_holder_addresses_type_title
    }

    override fun areItemsTheSame(item: Item): Boolean {
        if (item !is AddressFlowTitle) return false

        return this == item
    }

}


