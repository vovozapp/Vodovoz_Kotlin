package com.vodovoz.app.ui.model.custom

import com.vodovoz.app.ui.model.FilterPriceUI
import com.vodovoz.app.ui.model.FilterUI
import java.io.Serializable

class FilterBundleUI(
    val filterPriceUI: FilterPriceUI = FilterPriceUI(),
    val filterUIList: MutableList<FilterUI> = mutableListOf()
): Serializable