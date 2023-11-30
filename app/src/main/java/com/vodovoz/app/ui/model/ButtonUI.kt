package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ButtonUI(
    val name: String,
    val background: String,
    val textColor: String
) : Parcelable
