package com.vodovoz.app.ui.model

import android.os.Parcelable
import com.vodovoz.app.data.model.common.ActionEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class BannerUI(
    val id: Long,
    val detailPicture: String,
    val actionEntity: ActionEntity? = null
): Parcelable