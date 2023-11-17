package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.AddressEntity
import com.vodovoz.app.ui.model.AddressUI

object AddressMapper {

    fun List<AddressEntity>.mapToUI() = map{ it.mapToUI() }

    fun AddressEntity.mapToUI() = AddressUI(
        id = id,
        type = type,
        fullAddress = fullAddress,
        phone = phone,
        name = name,
        email = email,
        locality = locality,
        street = street,
        house = house,
        entrance = entrance,
        floor = floor,
        flat = flat,
        comment = comment,
        length = length,
        latitude = coordinates.substringBefore(","),
        longitude = coordinates.substringAfter(","),
        newFullAddress = newFullAddress
    )

}