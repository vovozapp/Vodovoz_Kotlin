package com.vodovoz.app.util.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

fun TextInputLayout.textOrError(minLength: Int): String? {
    val text = editText?.text?.toString()?.trim().orEmpty()
    if (text.isBlank()) {
        error = "Заполните поле"
        return null
    }

    if (text.length < minLength) {
        error = "Должно быть минимум $minLength символов"
        return null
    }

    return text
}

fun TextInputLayout.textOrError(range: IntRange): String? {
    val text = editText?.text?.toString()?.trim().orEmpty()
    if (text.isBlank()) {
        error = "Заполните поле"
        return null
    }

    if (text.length < range.first || text.length > range.last) {
        error = "Должно быть от ${range.first} до ${range.last} символов"
        return null
    }

    return text
}

inline fun EditText.onDone(crossinline callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
            return@setOnEditorActionListener true
        }
        false
    }
}

fun EditText.textChangedFlow(): Flow<String> {
    return callbackFlow {
        val textChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (this@textChangedFlow.isFocused) {
                    trySendBlocking(s?.toString().orEmpty())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        this@textChangedFlow.addTextChangedListener(textChangedListener)
        awaitClose {
            this@textChangedFlow.removeTextChangedListener(textChangedListener)
        }
    }
}

fun EditText.clear() {
    text.clear()
}

fun View.keyboardVisibilityChanges(): Flow<Boolean> {
    return onPreDrawFlow()
        .map { isKeyboardVisible() }
        .distinctUntilChanged()
}

fun View.onPreDrawFlow(): Flow<Unit> {
    return callbackFlow {
        val onPreDrawListener = ViewTreeObserver.OnPreDrawListener {
            trySendBlocking(Unit)
            true
        }
        viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
        awaitClose {
            viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
        }
    }
}

fun View.isKeyboardVisible(): Boolean = ViewCompat.getRootWindowInsets(this)
    ?.isVisible(Type.ime())
    ?: false

fun TextInputEditText.openKeyboard() {
    requestFocus()
    val imm =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun Context.copyText(text: String) {
    val clipboard =
        this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("Label", text)
    clipboard.setPrimaryClip(clip)
}

fun Fragment.shareText(text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}

fun String.toUnderline() : SpannableString {
    val s = SpannableString(this)
    s.setSpan(UnderlineSpan(), 0, this.length, 0)
    return s
}