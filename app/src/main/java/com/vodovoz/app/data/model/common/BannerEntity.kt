package com.vodovoz.app.data.model.common

import android.os.Parcelable
import java.io.Serializable

data class BannerEntity(
    val id: Long,
    val name: String,
    val detailPicture: String,
    val bannerActionEntity: BannerActionEntity? = null
): Serializable