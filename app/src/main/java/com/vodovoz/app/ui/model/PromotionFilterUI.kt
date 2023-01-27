package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PromotionFilterUI(
    val id: Long,
    val name: String,
    val code: String
): Parcelable

@Parcelize
class ListOfPromotionFilterUi : ArrayList<PromotionFilterUI>(), Parcelable