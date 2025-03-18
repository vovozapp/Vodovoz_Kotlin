package com.vodovoz.app.design_system.model.filters

data class FiltersPriceUi(
    val min: Int,
    val max: Int,
    val currentMin: Int = min,
    val currentMax: Int = max
){
    companion object {
        val Empty = FiltersPriceUi(min = 0, max = Int.MAX_VALUE)
    }
}
