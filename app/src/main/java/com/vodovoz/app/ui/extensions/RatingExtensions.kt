package com.vodovoz.app.ui.extensions

import android.widget.RatingBar

object RatingExtensions {

    fun RatingBar.setProgress(rating: Double) {
        progress = (rating * 20.0).toInt()
    }

}