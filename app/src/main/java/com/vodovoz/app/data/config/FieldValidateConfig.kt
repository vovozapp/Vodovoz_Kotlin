package com.vodovoz.app.data.config

object FieldValidateConfig {

    val MIN_COMMENT_LENGTH = 30

    val FIRST_NAME_LENGTH = 1..20
    val SECOND_NAME_LENGTH = 1..30
    val PASSWORD_LENGTH = 6..30
    val PHONE_REGEX = Regex("\\+7-[0-9]{3}-([0-9]{3})-([0-9]{2})-([0-9]{2})")
    val EMAIL_REGEX =
        Regex("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

    val LOCALITY_LENGTH = 1..20
    val STREET_LENGTH = 1..20
    val HOUSE_LENGTH = 1..20
    val ENTRANCE_LENGTH = 1..20
    val FLOOR_LENGTH = 1..20
    val OFFICE_LENGTH = 1..20

}