package com.vodovoz.app.design_system.text

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0, 8) else text.text
        var output = ""
        for (i in trimmed.indices) {
            output += trimmed[i]
            if (i == 1 || i == 3) {
                output += "."
            }
        }
        val dateTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 1 -> offset
                    offset in 2..3 -> offset + 1
                    offset in 4..7 -> offset + 2
                    else -> output.length
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 1 -> offset
                    offset in 2..4 -> offset - 1
                    offset in 5..9 -> offset - 2
                    else -> trimmed.length
                }
            }
        }

        return TransformedText(
            AnnotatedString(output),
            dateTranslator
        )
    }
}