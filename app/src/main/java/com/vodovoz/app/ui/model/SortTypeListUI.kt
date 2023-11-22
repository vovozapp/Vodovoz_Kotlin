package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SortTypeListUI(
    val name: String = "",
    var sortTypeList: List<SortTypeUI> = listOf(),
) : Parcelable
