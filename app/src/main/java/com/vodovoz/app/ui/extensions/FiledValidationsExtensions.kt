package com.vodovoz.app.ui.extensions

import android.text.InputFilter
import android.text.InputType
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.vodovoz.app.ui.extensions.TextViewExtensions.setPhoneValidator
import com.vodovoz.app.util.FieldValidationsSettings.EMAIL_REGEX
import com.vodovoz.app.util.FieldValidationsSettings.NAME_LENGTH
import com.vodovoz.app.util.FieldValidationsSettings.INVALID_COLOR_ID
import com.vodovoz.app.util.FieldValidationsSettings.PASSWORD_LENGTH
import com.vodovoz.app.util.FieldValidationsSettings.PHONE_REGEX
import com.vodovoz.app.util.FieldValidationsSettings.VALID_COLOR_ID
import com.vodovoz.app.util.LogSettings
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

object FiledValidationsExtensions {

    fun AppCompatEditText.setEmailValidation(
        tilEmail: TextInputLayout,
        compositeDisposable: CompositeDisposable,
        trackErrorSubject: PublishSubject<Boolean>,
        validEmailSubject: PublishSubject<Boolean>
    ) {
        var trackError = false
        trackErrorSubject.subscribeBy {
            trackError = it
            if (it) {
                when(EMAIL_REGEX.matches(this.text.toString())) {
                    true -> tilEmail.isErrorEnabled = false
                    false -> tilEmail.error = "Неправильный формат"
                }
            }
        }.addTo(compositeDisposable)

        this.doAfterTextChanged { input ->
            validEmailSubject.onNext(EMAIL_REGEX.matches(input.toString()))
            if (trackError) {
                when(EMAIL_REGEX.matches(input.toString())) {
                    true -> tilEmail.isErrorEnabled = false
                    false -> tilEmail.error = "Неправильный формат"
                }
            }
        }
    }

    fun AppCompatEditText.setPasswordValidation(
        tilPassword: TextInputLayout,
        compositeDisposable: CompositeDisposable,
        trackErrorSubject: PublishSubject<Boolean>,
        validPasswordSubject: PublishSubject<Boolean>,
        validateIfEmpty: Boolean = false
    ) {
        var trackError = false
        trackErrorSubject.subscribeBy {
            trackError = it
            if (it) {
                when(this.text.toString().isEmpty() && !validateIfEmpty) {
                    true -> {
                        tilPassword.isErrorEnabled = false
                        validPasswordSubject.onNext(true)
                    }
                    false -> when(PASSWORD_LENGTH.contains(this.text.toString().length)) {
                        true -> tilPassword.isErrorEnabled = false
                        false -> tilPassword.error = "Минимум ${PASSWORD_LENGTH.first} символов"
                    }
                }
            }
        }.addTo(compositeDisposable)

        this.doAfterTextChanged { input ->
            validPasswordSubject.onNext(PASSWORD_LENGTH.contains(input.toString().length))
            Log.d(LogSettings.DEVELOP_LOG, "${input.toString()} : ${PASSWORD_LENGTH.first} : ${PASSWORD_LENGTH.contains(input.toString().length)}")
            if (trackError) {
                when(this.text.toString().isEmpty()) {
                    true -> {
                        tilPassword.isErrorEnabled = false
                        validPasswordSubject.onNext(true)
                    }
                    false -> when(PASSWORD_LENGTH.contains(this.text.toString().length)) {
                        true -> tilPassword.isErrorEnabled = false
                        false -> tilPassword.error = "Минимум ${PASSWORD_LENGTH.first} символов"
                    }
                }
            }
        }
    }

    fun AppCompatEditText.setNameValidation(
        tilName: TextInputLayout,
        compositeDisposable: CompositeDisposable,
        trackErrorSubject: PublishSubject<Boolean>,
        validFirstNameSubject: PublishSubject<Boolean>
    ) {
        var trackError = false
        trackErrorSubject.subscribeBy {
            trackError = it
            if (it) {
                when(NAME_LENGTH.contains(this.text.toString().length)) {
                    true -> tilName.isErrorEnabled = false
                    false -> tilName.error = "Минимум ${NAME_LENGTH.first} символа"
                }
            }
        }.addTo(compositeDisposable)

        this.doAfterTextChanged { input ->
            validFirstNameSubject.onNext(NAME_LENGTH.contains(input.toString().length))
            if (trackError) {
                when(NAME_LENGTH.contains(input.toString().length)) {
                    true -> tilName.isErrorEnabled = false
                    false -> tilName.error = "Минимум ${NAME_LENGTH.first} символа"
                }
            }
        }
    }

    fun AppCompatEditText.setPhoneValidation(
        tilPhone: TextInputLayout,
        compositeDisposable: CompositeDisposable,
        trackErrorSubject: PublishSubject<Boolean>,
        validPhoneSubject: PublishSubject<Boolean>
    ) {
        var trackError = false
        trackErrorSubject.subscribeBy {
            trackError = it
            if (it) {
                when(PHONE_REGEX.matches(this.text.toString())) {
                    true -> tilPhone.isErrorEnabled = false
                    false -> tilPhone.error = "Неправильный формат"
                }
            }
        }.addTo(compositeDisposable)

        inputType = InputType.TYPE_CLASS_PHONE
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = InputFilter.LengthFilter(16)
        filters = fArray
        var oldLength = 0
        this.setOnFocusChangeListener { _, isFocused ->
            when(isFocused) {
                true -> {
                    if (text.toString().isEmpty()) setText("+7-")
                    setSelection(text.toString().length)
                }
                false -> {
                    if (text.toString() == "+7-") { setText("") }
                }
            }
        }

        this.doAfterTextChanged {
            if (this.isFocused) {
                it?.let { input ->
                    if (input.toString()[input.indices.last] == '-'
                        && input.indices.last != 2
                        && input.indices.last != 6
                        && input.indices.last != 10
                        && input.indices.last != 13
                    ) {
                        setText(StringBuilder().append(input).deleteAt(input.indices.last).toString())
                        setSelection(input.length - 1)
                    } else {
                        if (input.length < 3) {
                            setText("+7-")
                            setSelection(input.length + 1)
                        } else {
                            when(oldLength < input.length) {
                                true -> {
                                    when(input.indices.last) {
                                        5, 9, 12 -> {
                                            setText(StringBuilder().append(input).append("-").toString())
                                            oldLength = input.length + 1
                                            setSelection(input.length + 1)
                                        }
                                    }
                                }
                                false -> {
                                    when(input.indices.last) {
                                        5, 9, 12 -> {
                                            setText(StringBuilder().append(input).deleteAt(input.indices.last).toString())
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
            validPhoneSubject.onNext(PHONE_REGEX.matches(it.toString()))
            if (trackError) {
                when(PHONE_REGEX.matches(it.toString())) {
                    true -> tilPhone.isErrorEnabled = false
                    false -> tilPhone.error = "Неправильный формат"
                }
            }
        }
    }

}