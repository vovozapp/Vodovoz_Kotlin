package com.vodovoz.app.util

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import com.vodovoz.app.util.extensions.fromHtml

object SpanWithUrlHandler {
    fun setUnderButtonText(text: String, textView: TextView, onClick: (url: String?) -> Unit) {
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
                0, spanned.length,
                URLSpan::class.java
            )
            for (urlSpan in urlSpans){
                val start = spanned.getSpanStart(urlSpan)
                val end = spanned.getSpanEnd(urlSpan)
                val sequence = definition.substring(start, end)
                if (Character.isLetterOrDigit(sequence[0])) {
                    val clickSpan: ClickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            onClick(urlSpan.url)
                        }
                    }
                    spans.setSpan(
                        clickSpan, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        } else {
            textView.visibility = View.GONE
        }
    }
}