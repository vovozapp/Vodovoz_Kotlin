package com.vodovoz.app.data.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CatalogEntity(
    val categoryEntityList: List<CategoryEntity>,
    val topCatalogBanner: CatalogBanner? = null,
) : Parcelable