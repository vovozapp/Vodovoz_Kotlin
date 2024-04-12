package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class FilterValueEntity(
    val id: String,
    val value: String
): Parcelable