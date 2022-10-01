package com.vodovoz.app.util

object PhoneSingleFormatUtil {

    fun String.convertPhoneToBaseFormat(): String {
        if (this.isEmpty()) return ""
        var formatPhone: String
        formatPhone = this.replace("+7", "")
        formatPhone = formatPhone.replace("-", "")
        formatPhone = formatPhone.replace("(", "")
        formatPhone = formatPhone.replace(")", "")
        if (this.first() == '8') formatPhone = formatPhone.removePrefix("8")
        return formatPhone
    }

    fun String.convertPhoneToFullFormat(): String {
        if (this.isEmpty()) return ""
        val builder = StringBuilder()
        builder.append("+7-")
        if (this.length >= 1) builder.append(this[0])
        if (this.length >= 2) builder.append(this[1])
        if (this.length >= 3) builder.append(this[2])
        builder.append("-")
        if (this.length >= 4) builder.append(this[3])
        if (this.length >= 5) builder.append(this[4])
        if (this.length >= 6) builder.append(this[5])
        builder.append("-")
        if (this.length >= 7) builder.append(this[6])
        if (this.length >= 8) builder.append(this[7])
        builder.append("-")
        if (this.length >= 9) builder.append(this[8])
        if (this.length >= 10) builder.append(this[9])
        return builder.toString()
    }
}