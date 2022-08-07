package com.vodovoz.app.mapper

import com.vodovoz.app.data.model.common.UserDataEntity
import com.vodovoz.app.ui.fragment.user_data.Gender
import com.vodovoz.app.ui.model.UserDataUI

object UserDataMapper {

    fun UserDataEntity.mapToUI() = UserDataUI(
        id = id,
        firstName = firstName,
        secondName = secondName,
        gender = when(sex) {
            "Мужской" -> Gender.MALE
            "Женский" -> Gender.FEMALE
            else -> Gender.MALE
        },
        email = email,
        registerDate = registerDate,
        avatar = avatar,
        phone = phone,
        birthday = birthday,
        token = token
    )
}