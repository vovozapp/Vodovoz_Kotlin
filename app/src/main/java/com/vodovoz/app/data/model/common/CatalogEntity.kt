package com.vodovoz.app.data.model.common

import java.io.Serializable

class CatalogEntity (
    val categoryEntityList: List<CategoryEntity>,
    val topCatalogBanner: CatalogBanner? = null
): Serializable