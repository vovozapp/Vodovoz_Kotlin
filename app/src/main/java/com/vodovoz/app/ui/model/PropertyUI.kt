package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PropertyUI(
    val name: String,
    val value: String,
) : Parcelable