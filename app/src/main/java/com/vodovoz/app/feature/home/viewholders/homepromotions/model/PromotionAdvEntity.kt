package com.vodovoz.app.feature.home.viewholders.homepromotions.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PromotionAdvEntity(
    val titleHeader: String? = null,
    val titleAdv: String? = null,
    val bodyAdv: String? = null,
    val dataAdv: String? = null,
) : Parcelable
