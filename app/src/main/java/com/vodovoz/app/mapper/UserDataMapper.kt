package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.UserDataEntity
import com.vodovoz.app.ui.model.UserDataUI

object UserDataMapper {

    fun UserDataEntity.mapToUI() = UserDataUI(
        id = id,
        firstName = firstName,
        secondName = secondName,
        sex = sex,
        email = email,
        registerDate = registerDate,
        avatar = avatar,
        phone = phone,
        birthday = birthday,
        token = token
    )
}