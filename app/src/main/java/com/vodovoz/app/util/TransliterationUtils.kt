package com.vodovoz.app.util


data object TransliterationUtils {

    fun latinToCyrillic(text: String): String {
        val charMap = mapOf(
            "yo" to "ё", "zh" to "ж", "ts" to "ц", "ch" to "ч", "sh" to "ш",
            "sch" to "щ", "yu" to "ю", "ya" to "я",

            "Yo" to "Ё", "Zh" to "Ж", "Ts" to "Ц", "Ch" to "Ч", "Sh" to "Ш",
            "Sch" to "Щ", "Yu" to "Ю", "Ya" to "Я",

            "a" to "а", "b" to "б", "v" to "в", "g" to "г", "d" to "д", "e" to "е",
            "z" to "з", "i" to "и", "y" to "й", "k" to "к", "l" to "л", "m" to "м",
            "n" to "н", "o" to "о", "p" to "п", "r" to "р", "s" to "с", "t" to "т",
            "u" to "у", "f" to "ф", "h" to "х",

            "A" to "А", "B" to "Б", "V" to "В", "G" to "Г", "D" to "Д", "E" to "Е",
            "Z" to "З", "I" to "И", "Y" to "Й", "K" to "К", "L" to "Л", "M" to "М",
            "N" to "Н", "O" to "О", "P" to "П", "R" to "Р", "S" to "С", "T" to "Т",
            "U" to "У", "F" to "Ф", "H" to "Х"
        )

        var result = text
        for ((latin, cyrillic) in charMap) {
            result = result.replace(latin, cyrillic)
        }
        return result
    }

}

