package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CatalogBanner(
    val text: String = "",
    val textColor: String = "",
    val backgroundColor: String = "",
    val iconUrl: String? = null,
    val actionEntity: ActionEntity? = null
): Parcelable