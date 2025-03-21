package com.vodovoz.app.util


data object TransliterationUtils{

    fun cyrillicToLatin(text: String): String {
        val charMap = mapOf(
            'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g",
            'д' to "d", 'е' to "e", 'ё' to "yo", 'ж' to "zh",
            'з' to "z", 'и' to "i", 'й' to "y", 'к' to "k",
            'л' to "l", 'м' to "m", 'н' to "n", 'о' to "o",
            'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t",
            'у' to "u", 'ф' to "f", 'х' to "h", 'ц' to "ts",
            'ч' to "ch", 'ш' to "sh", 'щ' to "sch", 'ъ' to "",
            'ы' to "y", 'ь' to "", 'э' to "e", 'ю' to "yu",
            'я' to "ya",

            'А' to "A", 'Б' to "B", 'В' to "V", 'Г' to "G",
            'Д' to "D", 'Е' to "E", 'Ё' to "Yo", 'Ж' to "Zh",
            'З' to "Z", 'И' to "I", 'Й' to "Y", 'К' to "K",
            'Л' to "L", 'М' to "M", 'Н' to "N", 'О' to "O",
            'П' to "P", 'Р' to "R", 'С' to "S", 'Т' to "T",
            'У' to "U", 'Ф' to "F", 'Х' to "H", 'Ц' to "Ts",
            'Ч' to "Ch", 'Ш' to "Sh", 'Щ' to "Sch", 'Ъ' to "",
            'Ы' to "Y", 'Ь' to "", 'Э' to "E", 'Ю' to "Yu",
            'Я' to "Ya"
        )
        return text.map { char -> charMap[char] ?: char.toString() }.joinToString("")
    }

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

