package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PriceEntity(
    val price: Double,
    val oldPrice: Double,
    val requiredAmount: Int,
    val requiredAmountTo: Int,
) : Parcelable