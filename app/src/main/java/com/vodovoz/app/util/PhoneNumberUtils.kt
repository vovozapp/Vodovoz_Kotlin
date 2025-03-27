package com.vodovoz.app.util

import android.telephony.PhoneNumberUtils
import android.text.Selection
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.google.i18n.phonenumbers.PhoneNumberUtil

fun String.isValidRussianPhoneNumber(): Boolean{
    val phoneNumberUtil = PhoneNumberUtil.getInstance()
    return try {
        val number = phoneNumberUtil.parse(this, "RU")
        phoneNumberUtil.isValidNumber(number)
    } catch (e: Exception) {
        false
    }
}