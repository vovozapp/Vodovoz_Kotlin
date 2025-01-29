package com.vodovoz.app.util

import com.vodovoz.app.R

object FieldValidationsSettings {

    @JvmField
    val VALID_COLOR_ID: Int = R.color.text_gray
    @JvmField
    val INVALID_COLOR_ID: Int = R.color.red
    @JvmField
    val ACTIVATED_COLOR_ID: Int = R.color.bluePrimary

    const val MIN_COMMENT_LENGTH = 30

    val NAME_LENGTH = 2..20
    val PASSWORD_LENGTH = 6..30
    val PHONE_REGEX = Regex("\\+7\\(([0-9]{3})\\)([0-9]{3})-([0-9]{2})-([0-9]{2})")
    val EMAIL_REGEX = Regex("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")

    val LOCALITY_LENGTH = 1..40
    val STREET_LENGTH = 1..40
    val HOUSE_LENGTH = 1..20
    val ENTRANCE_LENGTH = 1..20
    val FLOOR_LENGTH = 1..20
    val OFFICE_LENGTH = 1..20

}