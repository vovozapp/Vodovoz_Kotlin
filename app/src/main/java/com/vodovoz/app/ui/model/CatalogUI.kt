package com.vodovoz.app.ui.model

data class CatalogUI(
    val categoryEntityList: List<CategoryUI> = listOf(),
    val topCatalogBanner: CatalogBannerUI? = null,
)
