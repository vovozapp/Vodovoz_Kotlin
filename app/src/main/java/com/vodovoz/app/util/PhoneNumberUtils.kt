package com.vodovoz.app.util

import com.google.i18n.phonenumbers.PhoneNumberUtil

fun String.isValidRussianPhoneNumber(): Boolean {
    val phoneNumberUtil = PhoneNumberUtil.getInstance()
    return try {
        val number = phoneNumberUtil.parse(this, "RU")
        phoneNumberUtil.isValidNumber(number)
    } catch (e: Exception) {
        false
    }
}


fun formatRussianPhoneNumber(input: String): String {
    val digits = input.replace("+7", "").filter { it ->
        it.isDigit()
    }.replaceFirstChar { char ->
        if (char == '7' || char == '8') "" else char.toString()
    }
    val mainNumber = digits.take(10)
    return "+7$mainNumber"
}