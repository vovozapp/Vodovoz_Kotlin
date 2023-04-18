package com.vodovoz.app.feature.map.test.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class UFDELIVERYPRICE(
    val `91853`: String?
) : Parcelable