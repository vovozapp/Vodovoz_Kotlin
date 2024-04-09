package com.vodovoz.app.ui.model.custom

import android.os.Parcelable
import com.vodovoz.app.ui.model.ProductUI
import kotlinx.parcelize.Parcelize

@Parcelize
class GiftProductUI(

    val title: String = "",
    val productsList: List<ProductUI> = listOf(),
): Parcelable
