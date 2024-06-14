package com.vodovoz.app.util

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import com.vodovoz.app.util.extensions.fromHtml

object SpanWithUrlHandler {
    fun setTextWithUrl(text: String, textView: TextView, callback: (url: String?, index: Int) -> Unit) {
        if (text.isNotEmpty()) {
            textView.visibility = View.VISIBLE
            textView.movementMethod = LinkMovementMethod.getInstance()
            val spanned = text.fromHtml()
            val definition = spanned.toString()
            textView.setText(
                definition,
                TextView.BufferType.SPANNABLE
            )
            val spans = textView.getText() as Spannable
            val urlSpans = spanned.getSpans(
                0,
                spanned.length,
                URLSpan::class.java
            )

            for ( urlSpan in urlSpans.withIndex()){
                val start = spanned.getSpanStart(urlSpan.value)
                val end = spanned.getSpanEnd(urlSpan.value)
                val sequence = definition.substring(start, end)
                if (Character.isLetterOrDigit(sequence[0])) {
                    val clickSpan: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            callback(urlSpan.value.url, urlSpan.index)
                        }
                    }
                    spans.setSpan(
                        clickSpan, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else {
                    spans.setSpan(
                        urlSpan.value, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        } else {
            textView.visibility = View.GONE
        }
    }
}