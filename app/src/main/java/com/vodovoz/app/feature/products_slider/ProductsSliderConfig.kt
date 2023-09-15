package com.vodovoz.app.feature.products_slider

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductsSliderConfig(
    val containShowAllButton: Boolean,
    val largeTitle: Boolean = false
) : Parcelable