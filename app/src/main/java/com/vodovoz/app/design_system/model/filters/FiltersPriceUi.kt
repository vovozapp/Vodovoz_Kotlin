package com.vodovoz.app.design_system.model.filters

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class FiltersPriceUi(
    val min: Int,
    val max: Int,
    val currentMin: Int = min,
    val currentMax: Int = max
): Parcelable {
    companion object {
        val Empty = FiltersPriceUi(min = Int.MAX_VALUE, max = Int.MIN_VALUE)
    }
}
