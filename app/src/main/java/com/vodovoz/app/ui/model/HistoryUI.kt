package com.vodovoz.app.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryUI(
    val id: Long,
    val detailPicture: String,
    val bannerUIList: List<BannerUI>
) : Parcelable