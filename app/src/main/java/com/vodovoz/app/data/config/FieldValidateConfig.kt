package com.vodovoz.app.data.config

object FieldValidateConfig {

    val FIRST_NAME_LENGTH = 1..20
    val SECOND_NAME_LENGTH = 1..30
    val PASSWORD_LENGTH = 6..30
    val PHONE_REGEX = Regex("\\+7-([0-9]{3})-([0-9]{3})-([0-9]{2})-([0-9]{2})")
    val EMAIL_REGEX =
        Regex("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

}