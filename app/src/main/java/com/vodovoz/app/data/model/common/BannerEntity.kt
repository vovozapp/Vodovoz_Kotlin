package com.vodovoz.app.data.model.common

import android.os.Parcelable
import com.vodovoz.app.feature.home.viewholders.homebanners.model.BannerAdvEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class BannerEntity(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val actionEntity: ActionEntity? = null,
    val bannerAdvEntity: BannerAdvEntity? = null,
) : Parcelable