package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LabelEntity(
    val name: String,
    val color: String,
): Parcelable
