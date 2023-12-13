package com.vodovoz.app.util.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Parcelable
import android.os.StrictMode
import android.provider.Settings
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Base64
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.BuildConfig
import com.google.android.material.snackbar.Snackbar
import com.vodovoz.app.R
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.ReadOnlyProperty


inline fun Fragment.addOnBackPressedCallback(crossinline callback: () -> Unit) {
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback.invoke()
        }
    }
    this.requireActivity().onBackPressedDispatcher.addCallback(
        this.viewLifecycleOwner,
        onBackPressedCallback
    )
}

fun Context.sp(value: Float) = (value * resources.displayMetrics.scaledDensity)

fun Context.dipF(value: Int): Float = (value * resources.displayMetrics.density)
fun Context.dipF(value: Float): Float = (value * resources.displayMetrics.density)
fun Context.dip(value: Int): Int = dipF(value).toInt()
fun Context.dip(value: Float): Int = dipF(value).toInt()
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

fun Context.color(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Context.string(@StringRes resId: Int, vararg formatArgs: Any): String {
    return if (formatArgs.isEmpty()) {
        getString(resId)
    } else {
        getString(resId, *formatArgs)
    }
}

fun Context.drawable(@DrawableRes drawableRes: Int) = ContextCompat.getDrawable(this, drawableRes)

fun String.isEmailCorrect(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches()
}

fun String.makeLink(context: Context, @ColorRes colorRes: Int, foo: () -> Unit): SpannableString {
    val spannableString = SpannableString(this)
    val clickableSpan = object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = ContextCompat.getColor(context, colorRes)
            ds.isUnderlineText = false
        }

        override fun onClick(view: View) {
            foo.invoke()
        }
    }
    val start = 0
    val end = this.length
    spannableString.setSpan(
        clickableSpan,
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

inline fun Activity.snack(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit = {},
) {
    /*val snack = Snackbar.make(findViewById(android.R.id.content), message, length)
    val view = snack.view
    view.background = this.drawable(R.drawable.bg_custom_snackbar)
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params
    snack.setTextColor(this.color(R.color.text_black))
    snack.f()
    snack.show()*/

    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
}

inline fun Activity.snackTop(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit = {},
) {
    val snack = Snackbar.make(findViewById(android.R.id.content), message, length)
    val view = snack.view
    view.background = this.drawable(R.drawable.bg_custom_snackbar)
    val params = view.layoutParams as FrameLayout.LayoutParams
    params.gravity = Gravity.TOP
    view.layoutParams = params
    snack.setTextColor(this.color(R.color.text_black))
    snack.f()
    snack.show()
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

fun startStrictMode() {
    if (BuildConfig.DEBUG) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
    }
}

fun View.hideKeyboard() {
    if (isKeyboardOpen()) {
        val activity = context?.unwrap() ?: return
        val window = activity.window

        WindowInsetsControllerCompat(window, this)
            .hide(WindowInsetsCompat.Type.ime())
    }
}

fun View.isKeyboardOpen(): Boolean {
    return ViewCompat.getRootWindowInsets(this)
        ?.isVisible(WindowInsetsCompat.Type.ime())
        ?: false
}

fun Context.unwrap(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextThemeWrapper -> this.unwrap()
        else -> null
    }
}

fun Activity.hideKeyboard() {
    var view = currentFocus
    if (view == null) {
        view = window.decorView
    }

    view.hideKeyboard()
}

fun File.toBase64(): String {
    FileInputStream(this.path).use {
        val bytes = it.readBytes()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}

fun NestedScrollView.scrollViewToTop() {
    this.post {
        this.fling(0)
        this.smoothScrollTo(0, 0)
    }
}

fun intArgs(key: String): ReadOnlyProperty<Fragment, Int> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key $key" }
        requireNotNull(args.getInt(key))
    }
}

fun longArgs(key: String): ReadOnlyProperty<Fragment, Long> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key $key" }
        requireNotNull(args.getLong(key))
    }
}

fun stringArgs(key: String): ReadOnlyProperty<Fragment, String> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key $key" }
        requireNotNull(args.getString(key))
    }
}

fun booleanArgs(key: String): ReadOnlyProperty<Fragment, Boolean> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key $key" }
        requireNotNull(args.getBoolean(key))
    }
}

fun <T : Parcelable> parcelableArgs(key: String): ReadOnlyProperty<Fragment, T> {
    return ReadOnlyProperty { thisRef, _ ->
        val args = thisRef.requireArguments()
        require(args.containsKey(key)) { "Arguments don't contain key $key" }
        @Suppress("UNCHECKED_CAST", "DEPRECATION")
        requireNotNull(args.getParcelable(key)) as T
    }
}

fun String.fromHtml(): Spanned {
     val result: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
         @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
    return result
}

fun openNotificationSettingsForApp(context: Context) {
    val intent = Intent().apply {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            else -> {
                action = "android.settings.APP_NOTIFICATION_SETTINGS"
                putExtra("app_package", context.packageName)
                putExtra("app_uid", context.applicationInfo.uid)
            }
        }
    }

    context.startActivity(intent)
}

fun Long.millisToItemDate(): String {
    val targetFormat = SimpleDateFormat("MMM_dd_yyyy_hh_mm_a", Locale.ROOT)
    return targetFormat.format(this)
}

inline fun LifecycleOwner.whenCreated(crossinline block: () -> Unit) {
    lifecycle.whenAtLeast(Lifecycle.State.CREATED, block)
}

inline fun LifecycleOwner.whenStarted(crossinline block: () -> Unit) {
    lifecycle.whenAtLeast(Lifecycle.State.STARTED, block)
}

inline fun Lifecycle.whenAtLeast(state: Lifecycle.State, crossinline block: () -> Unit) {
    if (currentState.isAtLeast(state)) {
        block.invoke()
    } else {
        val observer = object : LifecycleEventObserver {

            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when {
                    source.lifecycle.currentState.isAtLeast(state) -> {
                        block.invoke()
                        removeObserver(this)
                    }
                    source.lifecycle.currentState == Lifecycle.State.DESTROYED -> {
                        removeObserver(this)
                    }
                }
            }
        }

        addObserver(observer)
    }
}

fun EditText.updateText(text: String) {
    val focussed = hasFocus()
    if (focussed) {
        clearFocus()
    }
    setText(text)
    if (focussed) {
        requestFocus()
    }
}

fun fetchCurrentDayInTimeMillis(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun String.getColorWithAlpha(): Int {
    return ColorUtils.setAlphaComponent(
        Color.parseColor(this),
        (35f / 100 * 255).toInt()
    )
}

fun Activity.enableFullScreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    insetsController.hide(WindowInsetsCompat.Type.statusBars())
    insetsController.hide(WindowInsetsCompat.Type.navigationBars())
}

fun Activity.disableFullScreen() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.show(WindowInsetsCompat.Type.statusBars())
    insetsController.show(WindowInsetsCompat.Type.navigationBars())
}