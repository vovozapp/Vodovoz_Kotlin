package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PriceEntity(
    val price: Int,
    val oldPrice: Int,
    val requiredAmount: Int,
    val requiredAmountTo: Int,
) : Parcelable