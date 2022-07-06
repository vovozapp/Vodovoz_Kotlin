package com.vodovoz.app.ui.mapper

import com.vodovoz.app.data.model.common.AddressEntity
import com.vodovoz.app.ui.model.AddressUI

object AddressMapper {

    fun AddressEntity.mapToUI() = AddressUI(
        locality = locality,
        street = street,
        house = house,
        fullAddress = fullAddress
    )

}