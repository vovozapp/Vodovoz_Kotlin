package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PriceUI(
    val currentPrice: Int,
    val oldPrice: Int,
    val requiredAmount: Int
): Parcelable