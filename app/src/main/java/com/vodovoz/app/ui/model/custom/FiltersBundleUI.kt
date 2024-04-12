package com.vodovoz.app.ui.model.custom

import android.os.Parcelable
import com.vodovoz.app.ui.model.FilterPriceUI
import com.vodovoz.app.ui.model.FilterUI
import kotlinx.parcelize.Parcelize

@Parcelize
data class FiltersBundleUI(
    val filterPriceUI: FilterPriceUI = FilterPriceUI(),
    val filterUIList: MutableList<FilterUI> = mutableListOf(),
) : Parcelable