package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterPriceUI(
    var minPrice: Int = Int.MIN_VALUE,
    var maxPrice: Int = Int.MAX_VALUE,
) : Parcelable