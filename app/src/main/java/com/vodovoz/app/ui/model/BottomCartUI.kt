package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BottomCartUI(
    val totalSum: Float = 0f,
    val quantity: Int = 0,
    val productCount: Int = 0,
) : Parcelable
