package com.vodovoz.app.ui.model

import java.io.Serializable

class FilterPriceUI(
    var minPrice: Int = Int.MIN_VALUE,
    var maxPrice: Int = Int.MAX_VALUE
): Serializable