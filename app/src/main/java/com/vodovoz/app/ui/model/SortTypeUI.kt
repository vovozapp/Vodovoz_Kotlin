package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SortTypeUI(
    val sortName: String = "",
    val value: String = "",
    val orientation: String = ""
) : Parcelable
