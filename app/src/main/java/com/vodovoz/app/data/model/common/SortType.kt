package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SortType(
    val sortName: String = "Выбрать сортировку",
    val value: String = "",
    val orientation: String = ""
): Parcelable