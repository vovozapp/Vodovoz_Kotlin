package com.vodovoz.app.ui.model

import com.vodovoz.app.data.model.common.ActionEntity

data class CatalogBannerUI(
    val text: String = "Текст баннера",
    val textColor: String = "#FFFFFF",
    val backgroundColor: String = "#000000",
    val iconUrl: String? = null,
    val actionEntity: ActionEntity? = null
)
