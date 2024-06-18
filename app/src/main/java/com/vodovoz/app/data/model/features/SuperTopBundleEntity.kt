package com.vodovoz.app.data.model.features

import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.SectionsEntity

data class SuperTopBundleEntity(
    val categoryDetailsList: List<CategoryDetailEntity>?,
    val sectionsEntity: SectionsEntity?,
)
