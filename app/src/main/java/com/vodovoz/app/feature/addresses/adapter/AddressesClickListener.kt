package com.vodovoz.app.feature.addresses.adapter

import com.vodovoz.app.ui.model.AddressUI

interface AddressesClickListener {

    fun onAddressClick(item: AddressUI)
    fun onEditClick(item: AddressUI)
    fun onDelete(item: AddressUI)
}