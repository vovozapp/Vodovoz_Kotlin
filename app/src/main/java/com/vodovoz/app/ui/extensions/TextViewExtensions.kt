package com.vodovoz.app.ui.extensions

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.widget.EditText
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.doAfterTextChanged
import com.vodovoz.app.R


object TextViewExtensions {

//    fun TextView.setDrawableColor(@ColorRes color: Int) {
//        compoundDrawables.filterNotNull().forEach {
//            it.colorFilter =
//                PorterDuffColorFilter(getColor(context, R.color.green), PorterDuff.Mode.SRC_IN)
//        }
//    }

    fun EditText.setPhoneValidator(afterTextChange: (Editable?) -> Unit) {
        inputType = InputType.TYPE_CLASS_PHONE
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = LengthFilter(16)
        filters = fArray
        var oldLength = 0
        this.setOnFocusChangeListener { _, isFocused ->
            when (isFocused) {
                true -> {
                    if (text.isEmpty()) setText("+7-")
                    setSelection(text.length)
                }
                false -> {
                    if (text.toString() == "+7-") {
                        setText("")
                    }
                }
            }
        }
        this.doAfterTextChanged {
            if (this.isFocused) {
                setTextColor(getColor(this.context, R.color.text_black))
                it?.let { input ->
                    kotlin.runCatching {
                        if (input.toString()[input.indices.last] == '-'
                            && input.indices.last != 2
                            && input.indices.last != 6
                            && input.indices.last != 10
                            && input.indices.last != 13
                        ) {
                            kotlin.runCatching {
                                setText(
                                    StringBuilder().append(input).deleteAt(input.indices.last)
                                        .toString()
                                )
                                setSelection(input.length - 1)
                            }
                        } else {
                            if (input.length < 3
                                || input.toString() == "+7-7"
                                || input.toString() == "+7-8"
                                || input.toString() == "+7-+"
                            ) {
                                kotlin.runCatching {
                                    setText("+7-")
                                    setSelection(3)
                                }
                            } else {
                                kotlin.runCatching {
                                    when (oldLength < input.length) {
                                        true -> {
                                            when (input.indices.last) {
                                                5, 9, 12 -> {
                                                    setText(
                                                        StringBuilder().append(input).append("-")
                                                            .toString()
                                                    )
                                                    oldLength = input.length + 1
                                                    setSelection(input.length + 1)
                                                }
                                            }
                                        }
                                        false -> {
                                            when (input.indices.last) {
                                                5, 9, 12 -> {
                                                    setText(
                                                        StringBuilder().append(input)
                                                            .deleteAt(input.indices.last).toString()
                                                    )
                                                    oldLength = input.length - 1
                                                    setSelection(input.length - 1)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                afterTextChange(it)
            }
        }
    }


}